package com.ucdc.backend.domain.enums;

public enum Provider {
    ENEL,
    CODENSA,
    ENERGY_POWER,
    SUPERPOWER;


    public static boolean exists(String s){
        if (s==null) return false;
        try { valueOf(s.trim().toUpperCase()); return true; } catch(Exception e){ return false; }
    }
}
