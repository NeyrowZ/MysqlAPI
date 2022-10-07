package net.neyrowz.mysql.api;

import com.google.gson.Gson;
import net.neyrowz.mysql.api.annontations.Exclude;
import net.neyrowz.mysql.api.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class Mysql<T> {

    private Connection connection;
    private final Plugin plugin;
    private final Class<T> t;

    public Mysql(Plugin plugin, Class<T> t) {
            this.plugin = plugin;
        this.t = t;
    }

    public Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        try {
            FileConfiguration credentials = Utils.getCredentials(plugin);
            connection = DriverManager.getConnection("jdbc:mysql://" + credentials.getString("host") + "/" + credentials.getString("database"), credentials.getString("username"), credentials.getString("password"));
            System.out.println("\033[0;34m[\uD83D\uDCD8][Mysql] Successfully connected.\033[0m");
        } catch (SQLException e) {
            System.out.println("\033[0;31m[\uD83D\uDCD5][Mysql] Unable to connect.\033[0m");
            e.printStackTrace();
        }
        return connection;
    }

    public T getBeanFromDB(UUID uuid) {
        try {
            List<Field> fields = new ArrayList<>();
            for (Field field : t.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Exclude.class)) {
                    fields.add(field);
                }
            }
            Statement statement = getConnection().createStatement();
            String sql = "SELECT * FROM test WHERE uuid = \"" + uuid.toString() + "\"";
            ResultSet results = statement.executeQuery(sql);
            if (results.next()) {
                Gson gson = new Gson();
                StringBuilder str = new StringBuilder("{");
                for (Field field : fields) {
                    field.setAccessible(true);
                    str.append("\"");
                    str.append(field.getName());
                    str.append("\": \"");
                    str.append(results.getObject(field.getName()));
                    str.append("\"");
                    if (field == fields.toArray()[fields.toArray().length - 1]) {
                        str.append("}");
                    } else {
                        str.append(",");
                    }
                }
                statement.close();
                return gson.fromJson(str.toString(), t);
            } else {
                statement.close();
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertBeanInDB(T bean) {
        try {
            List<Field> fields = new ArrayList<>();
            for (Field field : t.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Exclude.class)) {
                    fields.add(field);
                }
            }
            StringBuilder str1 = new StringBuilder();
            for (Field field : fields) {
                field.setAccessible(true);
                str1.append(field.getName());
                if (field != fields.toArray()[fields.toArray().length - 1]) {
                    str1.append(",");
                }
            }
            StringBuilder str2 = new StringBuilder();
            for (Field field : fields) {
                field.setAccessible(true);
                str2.append("?");
                if (field != fields.toArray()[fields.toArray().length - 1]) {
                    str2.append(",");
                }
            }

            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO test(" + str1 + ") VALUES ("  + str2 + ")");
            int i = 0;
            System.out.println(fields.size());
            for (Field field : fields) {
                System.out.println(field.getName());
                i++;
                field.setAccessible(true);
                if (String.class.equals(field.getType())) {
                    statement.setString(i, (String)field.get(bean));
                } else if (UUID.class.equals(field.getType())) {
                    statement.setString(i, field.get(bean).toString());
                } else if (int.class.equals(field.getType())) {
                    statement.setInt(i, (int)field.get(bean));
                } else if (double.class.equals(field.getType())) {
                    statement.setDouble(i, (double)field.get(bean));
                }
            }
            statement.executeUpdate();
            statement.close();
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setupTable() {
        try {
            StringBuilder str = new StringBuilder();
            List<Field> fields = new ArrayList<>();
            for (Field field : t.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Exclude.class)) {
                    fields.add(field);
                }
            }
            for (Field field : fields) {
                field.setAccessible(true);
                str.append(field.getName()).append(" ").append(Utils.classToMysqlType(field.getType()));
                if (fields.toArray().length >= 1) {
                    if (field == fields.toArray()[fields.toArray().length - 1]) {
                        str.append(")");
                    } else {
                        str.append(", ");
                    }
                }
            }
            Statement statement = getConnection().createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS test(" + str;
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            System.out.println("[Mysql] Unable to create the table \"test\".");
            e.printStackTrace();
        }
    }
}