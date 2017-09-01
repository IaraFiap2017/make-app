package com.photo.photoapp;

import com.microsoft.projectoxford.face.contract.FacialHair;
import com.microsoft.projectoxford.face.contract.Glasses;

public class ResultBuilderPTBR {

    private StringBuilder sb;

    private int round(double d) {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if(result < 0.5)
            return d < 0 ? -i : i;
        else
            return d < 0 ? -(i + 1) : i + 1;
    }

    public ResultBuilderPTBR() {
        sb = new StringBuilder();
    }

    public ResultBuilderPTBR addOnlyOneMsg() {
        sb.append("Bebê, só pode uma pessoa na foto...");
        return this;
    }

    public ResultBuilderPTBR addBadPhotoMsg() {
        sb.append("Ops, essa foto ficou ruim bebê. Me manda outra? Tenta tirar na vertical que fica melhor!");
        return this;
    }

    public ResultBuilderPTBR addGlasses(Glasses glasses) {
        if(!glasses.name().equals("NoGlasses")) {
            //TODO
        }
        return this;
    }

    public ResultBuilderPTBR addAge(Double age) {
        sb.append("Você tem uns " + round(age) + " anos");
        sb.append("\n");
        return this;
    }

    public ResultBuilderPTBR addGender(String gender) {
        sb.append("Aparenta ser " + (gender.equals("male") ? "homem" : "mulher"));
        sb.append("\n");
        return this;
    }

    public ResultBuilderPTBR addFacialHair(FacialHair facialHair) {
        if(facialHair.beard >= 0.4 && facialHair.moustache >= 0.5 && facialHair.sideburns >= 0.3){
            sb.append("Tem barba, bigode e costeleta");
            sb.append("\n");

        } else {
            sb.append(facialHair.beard >= 0.4 ? "Tem barba \n" : "");
            sb.append(facialHair.moustache >= 0.5 ? "Tem bigode \n" : "");
            sb.append(facialHair.sideburns >= 0.3 ? "Tem costeleta \n" : "");
        }
        return this;
    }

    public String build() {
        return sb.toString();
    }
}
