package com.sante.app.api;

public class AppointmentRequest {
    private int    doctorId;
    private String patientNom;
    private String date;
    private String heure;
    private String motif;

    public AppointmentRequest(int doctorId, String patientNom,
                              String date, String heure, String motif) {
        this.doctorId   = doctorId;
        this.patientNom = patientNom;
        this.date       = date;
        this.heure      = heure;
        this.motif      = motif;
    }

    public int    getDoctorId()   { return doctorId; }
    public String getPatientNom() { return patientNom; }
    public String getDate()       { return date; }
    public String getHeure()      { return heure; }
    public String getMotif()      { return motif; }
}
