package com.example.voogle.PojoClasses;

public class Fares {
    UberxFares uberx;
    UberMotoFares ubermoto;
    PathaoCarFares pathaocar;
    PathaoBikeFares pathaobike;

    public UberxFares getUberx() {
        return uberx;
    }

    public void setUberx(UberxFares uberx) {
        this.uberx = uberx;
    }

    public UberMotoFares getUbermoto() {
        return ubermoto;
    }

    public void setUbermoto(UberMotoFares ubermoto) {
        this.ubermoto = ubermoto;
    }

    public PathaoCarFares getPathaocar() {
        return pathaocar;
    }

    public void setPathaocar(PathaoCarFares pathaocar) {
        this.pathaocar = pathaocar;
    }

    public PathaoBikeFares getPathaobike() {
        return pathaobike;
    }

    public void setPathaobike(PathaoBikeFares pathaobike) {
        this.pathaobike = pathaobike;
    }
}
