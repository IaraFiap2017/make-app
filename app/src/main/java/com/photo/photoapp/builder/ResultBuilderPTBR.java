package com.photo.photoapp.builder;

import com.microsoft.projectoxford.face.contract.FacialHair;
import com.microsoft.projectoxford.face.contract.Glasses;
import com.microsoft.projectoxford.face.contract.HeadPose;

public class ResultBuilderPTBR {

    private StringBuilder sb;

    public ResultBuilderPTBR() {
        sb = new StringBuilder();
    }

    private int round(double d) {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if(result < 0.5)
            return d < 0 ? -i : i;
        else
            return d < 0 ? -(i + 1) : i + 1;
    }

    private int ageAdjuster(int age) {
        if(age > 18 && age < 23)
            return age - 1;
        else if(age >= 23)
            return age - 2;
        return age;
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

    public ResultBuilderPTBR addNoIndexImageError() {
        sb.append("Ops! Manda uma imagem do whats, da net ou da sua câmera!");
        return this;
    }

    public ResultBuilderPTBR addNoInternetError() {
        sb.append("Ops! Estamos sem internet. Ative o 3G/4G ou wi-fi");
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
                sb.append("Está usando óculos de sol \n");
        }
        return this;
    }

    public ResultBuilderPTBR addAgeAndGender(Double age, String gender) {
        sb.append("Você aparenta " + ageAdjuster(round(age)) + " anos, " + (gender.equals("male") ? "homem" : "mulher"));
        sb.append("\n");
        return this;
    }

    public ResultBuilderPTBR addFacialHair(FacialHair facialHair, String gender) {
        if(gender.equals("male")) {
            if(facialHair.beard >= 0.3 && facialHair.moustache >= 0.4 && facialHair.sideburns >= 0.2){
                sb.append("Tem barba, bigode e costeleta");
                sb.append("\n");

            } else {
                sb.append(facialHair.beard >= 0.3 ? "Tem barba \n" : "");
                sb.append(facialHair.moustache >= 0.4 ? "Tem bigode \n" : "");
                sb.append(facialHair.sideburns >= 0.2 ? "Tem costeleta \n" : "");
            }
        }
        return this;
    }

    public ResultBuilderPTBR addSmile(Double smile) {
        if(smile > 0.91) {
            sb.append("Tem um belo sorriso");
            sb.append("\n");
        }

        return this;
    }

    public ResultBuilderPTBR addHeadPose(HeadPose headPose) {
        if(headPose.yaw > 15 || headPose.yaw < -15) {
            sb.append("Rosto de ladinho, hummm...");
            sb.append("\n");
        }
        return this;
    }

    public String build() {
        return sb.toString();
    }
}
