package fr.epsi.services;

import fr.epsi.DBConnection;
import fr.epsi.entities.Demand;
import fr.epsi.entities.DemandInfo;
import fr.epsi.repositories.DemandRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DemandService {
    DemandRepository demandRepository = new DemandRepository();

    public void createDemand(Demand demand) throws SQLException {
        demandRepository.create(demand);
    }

    public List<Demand> getAllDemands() throws SQLException {
        return demandRepository.findAll();
    }

    public List<Demand> getDemandAfterDate(Timestamp date) throws SQLException {
        return demandRepository.getDemandsAfterDate(date);
    }

    public List<DemandInfo> getDemandInfo(String id) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC GetDemandInfo @demandId = ?;"))
        {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            List<DemandInfo> demands = new ArrayList<>();

            while (rs.next()) {
                demands.add(setInfoData(rs));
            }

            return demands;
        }
    }

    public DemandInfo setInfoData(ResultSet rs) throws SQLException {
        return new DemandInfo()
                .setDemandDate(rs.getTimestamp("demand_date"))
                .setProjectName(rs.getString(2))
                .setSiteName(rs.getString(3))
                .setUsername(rs.getString("username"))
                .setUserProfessionLabel(rs.getString(5))
                .setSubcontractorName(rs.getString(6))
                .setSubcontractorProfessionLabel(rs.getString(7))
                .setPath(rs.getString("path"));
    }
}
