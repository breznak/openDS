
package cz.cvut.cognitive.distractors;

/**
 *
 * @author Johnny
 * 
 * Class containing all settings of distraction-related classes for easy access.
 * Contains relevant variables and getters with setters:
 * 
 * Boolean variables of active / nonactive distractions
 * Float variables of probabilities of these distractions
 * 
 */
public class DistractionSettings {
    
    public static boolean Rain;
    public static boolean Snow;
    public static boolean Fog;
    public static boolean Sound;
    public static boolean Box;
    public static boolean Text;
    public static boolean Pedestrian;
    public static boolean Dark;
    public static boolean Collect;
    public static float intensityRain;
    public static float intensitySnow;
    public static float intensityFog;
    public static float probabilitySound;
    public static float probabilityBox;
    public static float probabilityText;
    public static float probabilityPedestrian;
    public static float probabilityDark;
    public static float probabilityCollect;
    public static int distRunning;
    public static boolean distScenario;
    public static boolean questionAnswered;

    public static boolean isCollect() {
        return Collect;
    }

    public static void setCollect(boolean Collect) {
        DistractionSettings.Collect = Collect;
    }

    public static float getProbabilityCollect() {
        return probabilityCollect;
    }

    public static void setProbabilityCollect(float probabilityCollect) {
        DistractionSettings.probabilityCollect = probabilityCollect;
    }    
    
    public static boolean isQuestionAnswered() {
        return questionAnswered;
    }

    public static void setQuestionAnswered(boolean questionAnswered) {
        DistractionSettings.questionAnswered = questionAnswered;
    }

    public static boolean isDistScenario() {
        return distScenario;
    }

    public static void setDistScenario(boolean distScenario) {
        DistractionSettings.distScenario = distScenario;
    }

    public static boolean isRain() {
        return Rain;
    }

    public static void setRain(boolean Rain) {
        DistractionSettings.Rain = Rain;
    }

    public static boolean isSnow() {
        return Snow;
    }

    public static void setSnow(boolean Snow) {
        DistractionSettings.Snow = Snow;
    }

    public static boolean isFog() {
        return Fog;
    }

    public static void setFog(boolean Fog) {
        DistractionSettings.Fog = Fog;
    }

    public static boolean isSound() {
        return Sound;
    }

    public static void setSound(boolean Sound) {
        DistractionSettings.Sound = Sound;
    }

    public static boolean isBox() {
        return Box;
    }

    public static void setBox(boolean Box) {
        DistractionSettings.Box = Box;
    }

    public static boolean isText() {
        return Text;
    }

    public static void setText(boolean Text) {
        DistractionSettings.Text = Text;
    }

    public static boolean isPedestrian() {
        return Pedestrian;
    }

    public static void setPedestrian(boolean Pedestrian) {
        DistractionSettings.Pedestrian = Pedestrian;
    }

    public static boolean isDark() {
        return Dark;
    }

    public static void setDark(boolean Dark) {
        DistractionSettings.Dark = Dark;
    }

    public static float getProbabilityPedestrian() {
        return probabilityPedestrian;
    }

    public static void setProbabilityPedestrian(float propabilityPedestrian) {
        DistractionSettings.probabilityPedestrian = propabilityPedestrian;
    }

    public static float getProbabilityDark() {
        return probabilityDark;
    }

    public static void setProbabilityDark(float propabilityDark) {
        DistractionSettings.probabilityDark = propabilityDark;
    }

    public static float getIntensityRain() {
        return intensityRain;
    }

    public static void setIntensityRain(float intensityRain) {
        DistractionSettings.intensityRain = intensityRain;
    }

    public static float getIntensitySnow() {
        return intensitySnow;
    }

    public static void setIntensitySnow(float intensitySnow) {
        DistractionSettings.intensitySnow = intensitySnow;
    }

    public static float getIntensityFog() {
        return intensityFog;
    }

    public static void setIntensityFog(float intensityFog) {
        DistractionSettings.intensityFog = intensityFog;
    }
    
    public static float getProbabilitySound() {
        return probabilitySound;
    }

    public static void setProbabilitySound(float propabilitySound) {
        DistractionSettings.probabilitySound = propabilitySound;
    }

    public static float getProbabilityBox() {
        return probabilityBox;
    }

    public static void setProbabilityBox(float propabilityBox) {
        DistractionSettings.probabilityBox = propabilityBox;
    }

    public static float getProbabilityText() {
        return probabilityText;
    }

    public static void setProbabilityText(float propabilityText) {
        DistractionSettings.probabilityText = propabilityText;
    }   
}
