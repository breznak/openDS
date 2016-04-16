
package distraction;

/**
 *
 * @author Johnny
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
    public static float intensityRain;
    public static float intensitySnow;
    public static float intensityFog;
    public static float propabilitySound;
    public static float propabilityBox;
    public static float propabilityText;
    public static float propabilityPedestrian;
    public static float propabilityDark;
    public static boolean distRunning;

    public static boolean isDistRunning() {
        return distRunning;
    }

    public static void setDistRunning(boolean distIsRunning) {
        DistractionSettings.distRunning = distIsRunning;
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

    public static float getPropabilityPedestrian() {
        return propabilityPedestrian;
    }

    public static void setPropabilityPedestrian(float propabilityPedestrian) {
        DistractionSettings.propabilityPedestrian = propabilityPedestrian;
    }

    public static float getPropabilityDark() {
        return propabilityDark;
    }

    public static void setPropabilityDark(float propabilityDark) {
        DistractionSettings.propabilityDark = propabilityDark;
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
    
    public static float getPropabilitySound() {
        return propabilitySound;
    }

    public static void setPropabilitySound(float propabilitySound) {
        DistractionSettings.propabilitySound = propabilitySound;
    }

    public static float getPropabilityBox() {
        return propabilityBox;
    }

    public static void setPropabilityBox(float propabilityBox) {
        DistractionSettings.propabilityBox = propabilityBox;
    }

    public static float getPropabilityText() {
        return propabilityText;
    }

    public static void setPropabilityText(float propabilityText) {
        DistractionSettings.propabilityText = propabilityText;
    }

    

    
    
    
}
