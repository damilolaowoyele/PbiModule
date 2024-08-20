package org.fhi360.PbiModule.Model.StatesLgasFacilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class LgasFacilitiesJsonUserType implements UserType<LgasFacilities> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public int getSqlType() {
        return Types.OTHER; // PostgreSQL JSON type is mapped to java.sql.Types.OTHER
    }

    @Override
    public Class<LgasFacilities> returnedClass() {
        return LgasFacilities.class;
    }

    @Override
    public boolean equals(LgasFacilities x, LgasFacilities y) {
        return x == null ? y == null : x.equals(y);
    }

    @Override
    public int hashCode(LgasFacilities x) {
        return x != null ? x.hashCode() : 0;
    }

    @Override
    public LgasFacilities nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        PGobject pgObject = (PGobject) rs.getObject(position);
        if (pgObject == null) {
            return null;
        }
        try {
            return objectMapper.readValue(pgObject.getValue(), LgasFacilities.class);
        } catch (IOException e) {
            throw new SQLException("Failed to convert String to LgasFacilities: " + e.getMessage(), e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, LgasFacilities value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            try {
                PGobject pgObject = new PGobject();
                pgObject.setType("json");
                pgObject.setValue(objectMapper.writeValueAsString(value));
                st.setObject(index, pgObject);
            } catch (IOException e) {
                throw new SQLException("Failed to convert LgasFacilities to String: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public LgasFacilities deepCopy(LgasFacilities value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(value), LgasFacilities.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deep copy LgasFacilities: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(LgasFacilities value) {
        return (Serializable) deepCopy(value);
    }

    @Override
    public LgasFacilities assemble(Serializable cached, Object owner) {
        return deepCopy((LgasFacilities) cached);
    }

    @Override
    public LgasFacilities replace(LgasFacilities original, LgasFacilities target, Object owner) {
        return deepCopy(original);
    }
}



