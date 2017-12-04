package com.zm.piboxservice.database;

import com.google.gson.Gson;
import com.zm.pibox.model.HVACConfiguration;
import com.zm.pibox.model.IrrigationConfiguration;
import com.zm.pibox.model.LightConfiguration;

import java.sql.*;

public class Configuration {

    private static Configuration _instance;
    private Connection _db;
    private Gson _gson;
    private ThreadLocal<Exception> lastError = new ThreadLocal<>();

    private Configuration(Connection db) {
        _db = db;
        _gson = new Gson();
    }

    public static Configuration instance() {
        if(_instance == null) {
            try {
                Connection db = DriverManager.getConnection("jdbc:sqlite:C:/sqlite/pibox");
                _instance = new Configuration(db);
                _instance.createTable();
            } catch (SQLException e) {
                //TODO: handle this better
                e.printStackTrace();
            }
        }

        return _instance;
    }

    public static LightConfiguration getOrDefault(LightConfiguration configuration) {
        configuration = configuration == null ? new LightConfiguration() : configuration;
        if(configuration.getOff() == null) configuration.setOff(new Time(0));
        if(configuration.getOn() == null) configuration.setOn(new Time(0));
        return configuration;
    }

    public static IrrigationConfiguration getOrDefault(IrrigationConfiguration configuration) {
        if(configuration == null) {
            configuration = new IrrigationConfiguration();
            configuration.setMinimum(Integer.MIN_VALUE);
        }

        return configuration;
    }

    public static HVACConfiguration getOrDefault(HVACConfiguration configuration) {
        if(configuration == null) {
            configuration = new HVACConfiguration();
            configuration.setMinimum(Integer.MIN_VALUE);
            configuration.setMaximum(Integer.MAX_VALUE);
        }

        return configuration;
    }

    public LightConfiguration getLightConfiguration() {
        LightConfiguration retval = getConfiguration("light_configuration", LightConfiguration.class);
        return getOrDefault(retval);
    }

    public boolean setLightConfiguration(LightConfiguration configuration) {
        return setConfiguration("light_configuration", configuration);
    }

    public IrrigationConfiguration getIrrigationConfiguration() {
        IrrigationConfiguration retval = getConfiguration("irrigation_configuration", IrrigationConfiguration.class);
        return getOrDefault(retval);
    }

    public boolean setIrrigationConfiguration(IrrigationConfiguration configuration) {
        return setConfiguration("irrigation_configuration", configuration);
    }

    public HVACConfiguration getHVACConfiguration() {
        HVACConfiguration retval =  getConfiguration("hvac_configuration", HVACConfiguration.class);
        return getOrDefault(retval);
    }

    public boolean setHVACConfiguration(HVACConfiguration configuration) {
        return setConfiguration("hvac_configuration", configuration);
    }

    private <T> T getConfiguration(String key, Class<T> clazz) {
        String sql =
                  "SELECT value \n"
                + "FROM configuration\n"
                + "WHERE key = '" + key + "';";

        try {
            PreparedStatement stmt = _db.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                String value = rs.getString("value");
                return _gson.fromJson(value, clazz);
            }
        }

        catch(SQLException e) {
            e.printStackTrace();
            lastError.set(e);
        }

        return null;
    }

    public <T> boolean setConfiguration(String key, T configuration) {
        String sql =
                  "INSERT OR REPLACE INTO configuration\n"
                + "(key, value)\n"
                + "VALUES ('" + key + "', ?)\n";

        try {
            String value = _gson.toJson(configuration);
            PreparedStatement stmt = _db.prepareStatement(sql);
            stmt.setString(1, value);
            stmt.setString(1, value);
            stmt.execute();
            return true;

        } catch(SQLException e) {
            e.printStackTrace();
            lastError.set(e);
            return false;
        }
    }

    public Exception getLastError() {
        return lastError.get();
    }

    private void  createTable() throws SQLException {
        PreparedStatement stmt = _db.prepareStatement(
                "CREATE TABLE IF NOT EXISTS configuration (\n"
                        + "	  key string PRIMARY KEY,\n"
                        + "	  value string NOT NULL"
                        + ");"
        );

        stmt.execute();
    }
}
