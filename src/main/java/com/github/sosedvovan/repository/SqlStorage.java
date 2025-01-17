package com.github.sosedvovan.repository;

import com.github.sosedvovan.model.Zapis;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class SqlStorage {

//    private AtomicInteger counter = new AtomicInteger(0);

    String dbUrl = "jdbc:postgresql://localhost:5432/zametki";
    String dbUser = "postgres";
    String dbPassword = "00821821";

//    Connection connection;

    {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        try {
//            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
    }

    //    public void clear(){
//        try {
//            PreparedStatement ps = connection.prepareStatement("DELETE FROM zametki");
//            ps.execute();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
    public void save(Zapis z) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement("INSERT INTO zametki (id, datetime, description1, description2) VALUES (?,?,?,?)")) {
            int id = getAndIncrement();
            ps.setInt(1, id);
            ps.setString(2, z.getDateTime());
            ps.setString(3, z.getDescription1());
            ps.setString(4, z.getDescription2());
            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private int getAndIncrement() {
        int counter = 0;
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement psMaxCounter = connection.prepareStatement("SELECT MAX(id) as max FROM zametki")) {
            ResultSet rs = psMaxCounter.executeQuery();
            rs.next();
//            String maxcounter = rs.getString(1);
            Optional<String> optional = Optional.ofNullable(rs.getString(1));
            if (optional.isPresent()) {
                counter = (Integer.parseInt(optional.get())) + 1;
            }else counter = 1;
//            System.out.println(maxcounter);
//            counter = maxcounter.equals(null) ? 1 : Integer.parseInt(maxcounter);


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return counter;
    }


    public List<Zapis> getAll() {
        List<Zapis> allZapis = new LinkedList<>();
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM zametki ORDER BY id")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Zapis z = new Zapis(rs.getInt("id"), rs.getString("dateTime"),
                        rs.getString("description1"), rs.getString("description2"));
                allZapis.add(z);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();

        }

        System.out.println("Служебное сообщение List<Zapis>: " + allZapis);

        return allZapis;
    }

    public void delete(Integer id) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement("DELETE FROM zametki WHERE id=?")) {
            ps.setInt(1, id);
            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(Zapis newZ) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE zametki SET id=?, dateTime = ?, description1=?, description2=? WHERE id = ?")) {
            ps.setInt(1, newZ.getId());
            ps.setString(2, newZ.getDateTime());
            ps.setString(3, newZ.getDescription1());
            ps.setString(4, newZ.getDescription2());
            ps.setInt(5, newZ.getId());
            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Zapis get(String id) {
        Zapis z = Zapis.EMPTY;
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM zametki WHERE id =?")) {
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            rs.next();
            z = new Zapis(rs.getInt("id"), rs.getString("dateTime"),
                    rs.getString("description1"), rs.getString("description2"));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return z == null ? Zapis.EMPTY : z;
    }
}
