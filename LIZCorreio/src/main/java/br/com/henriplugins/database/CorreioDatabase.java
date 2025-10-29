package br.com.henriplugins.database;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CorreioDatabase {

    private final Connection conn;

    public CorreioDatabase(File dbFile) throws SQLException {
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        this.conn = DriverManager.getConnection(url);
        createTables();
    }

    private void createTables() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS correios (
                    key TEXT PRIMARY KEY,
                    world TEXT NOT NULL,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    z INTEGER NOT NULL,
                    owner TEXT NOT NULL
                )
            """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS encomendas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    owner_uuid TEXT NOT NULL,
                    item BLOB NOT NULL
                )
            """);
        }
    }

    public void insertCorreio(String key, String world, int x, int y, int z, UUID owner) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT OR REPLACE INTO correios(key, world, x, y, z, owner) VALUES(?,?,?,?,?,?)")) {
            ps.setString(1, key);
            ps.setString(2, world);
            ps.setInt(3, x);
            ps.setInt(4, y);
            ps.setInt(5, z);
            ps.setString(6, owner.toString());
            ps.executeUpdate();
        }
    }

    public void deleteCorreio(String key) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM correios WHERE key = ?")) {
            ps.setString(1, key);
            ps.executeUpdate();
        }
    }

    public List<CorreioRow> loadAllCorreios() throws SQLException {
        List<CorreioRow> rows = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT key, world, x, y, z, owner FROM correios")) {
            while (rs.next()) {
                rows.add(new CorreioRow(
                        rs.getString("key"),
                        rs.getString("world"),
                        rs.getInt("x"),
                        rs.getInt("y"),
                        rs.getInt("z"),
                        UUID.fromString(rs.getString("owner"))
                ));
            }
        }
        return rows;
    }

    public static final class CorreioRow {
        public final String key;
        public final String world;
        public final int x, y, z;
        public final UUID owner;
        public CorreioRow(String key, String world, int x, int y, int z, UUID owner) {
            this.key = key; this.world = world; this.x = x; this.y = y; this.z = z; this.owner = owner;
        }
    }

    public void addEncomenda(UUID owner, ItemStack item) throws SQLException, IOException {
        byte[] blob = serialize(item);
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO encomendas(owner_uuid, item) VALUES(?,?)")) {
            ps.setString(1, owner.toString());
            ps.setBytes(2, blob);
            ps.executeUpdate();
        }
    }

    public List<ItemStack> getEncomendas(UUID owner) throws SQLException, IOException, ClassNotFoundException {
        List<ItemStack> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT item FROM encomendas WHERE owner_uuid = ?")) {
            ps.setString(1, owner.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    byte[] blob = rs.getBytes("item");
                    ItemStack it = deserialize(blob);
                    if (it != null) list.add(it);
                }
            }
        }
        return list;
    }

    public void deleteEncomenda(UUID owner, ItemStack item) throws SQLException, IOException {
        byte[] target = serialize(item);
        try (PreparedStatement ps = conn.prepareStatement("SELECT id, item FROM encomendas WHERE owner_uuid = ?")) {
            ps.setString(1, owner.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    byte[] blob = rs.getBytes("item");
                    if (java.util.Arrays.equals(blob, target)) {
                        int id = rs.getInt("id");
                        try (PreparedStatement del = conn.prepareStatement("DELETE FROM encomendas WHERE id = ?")) {
                            del.setInt(1, id);
                            del.executeUpdate();
                        }
                        return;
                    }
                }
            }
        }
    }

    private byte[] serialize(ItemStack item) throws IOException {
        if (item == null) return new byte[0];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos)) {
            boos.writeObject(item);
            boos.flush();
            return baos.toByteArray();
        }
    }

    private ItemStack deserialize(byte[] data) throws IOException, ClassNotFoundException {
        if (data == null || data.length == 0) return null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             BukkitObjectInputStream bois = new BukkitObjectInputStream(bais)) {
            return (ItemStack) bois.readObject();
        }
    }

    public void close() throws SQLException { conn.close(); }
}
