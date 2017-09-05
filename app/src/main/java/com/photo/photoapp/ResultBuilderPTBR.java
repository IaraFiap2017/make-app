package com.photo.photoapp;

import com.microsoft.projectoxford.face.contract.FacialHair;
import com.microsoft.projectoxford.face.contract.Glasses;
import com.microsoft.projectoxford.face.contract.HeadPose;

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

    public ResultBuilderPTBR addPermissionMessage() {
        sb.append("Você precisa me autorizar para acessar sua câmera e gravar dados!");
        return this;
    }

    public ResultBuilderPTBR addNoPermissionClosesApp() {
        sb.append("Desculpe, sem permissão para acessar a câmera e gravar dados, não é possível usar o app");
        return this;
    }

    public ResultBuilderPTBR addErrorOnePersonAlowed() {
        sb.append("Bebê, só pode uma pessoa na foto...");
        return this;
    }

    public ResultBuilderPTBR addIOError() {
        sb.append("Ops deu erro! Pode mandar a foto denovo?");
        return this;
    }

    public ResultBuilderPTBR addErrorBadPhoto() {
        sb.append("Ops, a foto ficou ruim bebê. Manda outra? Tira bem perto da câmera e na vertical!");
        return this;
    }

    public ResultBuilderPTBR addGlasses(Glasses glasses) {
        if(!glasses.name().equals("NoGlasses")) {
            if(glasses.toString().equals("ReadingGlasses"))
                sb.append("Usa óculos \n");
            else if(glasses.toString().equals("Sunglasses"))
                sb.append("Está usando óculos de sol");
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

    public ResultBuilderPTBR addFacialHair(FacialHair facialHair, String gender) {
        if(gender.equals("male") && facialHair.beard >= 0.4 && facialHair.moustache >= 0.5 && facialHair.sideburns >= 0.3)
            sb.append("Não tem barba \n");

        else if(facialHair.beard >= 0.4 && facialHair.moustache >= 0.5 && facialHair.sideburns >= 0.3){
            sb.append("Tem barba, bigode e costeleta");
            sb.append("\n");

        } else {
            sb.append(facialHair.beard >= 0.4 ? "Tem barba \n" : "");
            sb.append(facialHair.moustache >= 0.5 ? "Tem bigode \n" : "");
            sb.append(facialHair.sideburns >= 0.3 ? "Tem costeleta \n" : "");
        }

        return this;
    }

    public ResultBuilderPTBR addSmile(Double smile) {
        if(smile > 0.9) {
            sb.append("Tem um belo sorriso");
            sb.append("\n");
        }

        return this;
    }

    public ResultBuilderPTBR addHeadPose(HeadPose headPose) {
        if(headPose.yaw > 10 || headPose.yaw < -10) {
            sb.append("Rosto de ladinho, hummm...");
            sb.append("\n");
        }

        return this;
    }

    public String build() {
        return sb.toString();
    }
}
