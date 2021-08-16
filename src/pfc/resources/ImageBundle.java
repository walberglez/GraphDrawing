/**
 * ImageBundle.java
 */
package pfc.resources;

import java.awt.*;
import java.net.*;
import java.util.*;

/**
 * @author Cameron Behar
 */
public class ImageBundle extends ResourceBundle {
    private static Map<String, Image> map = new HashMap<String, Image>();
    private static final ResourceBundle instance = ResourceBundle
            .getBundle("pfc.resources.ImageBundle");

    public static Image get(String key) {
        try {
            return (Image) instance.getObject(key);
        } catch (MissingResourceException ex) {
            System.out.println(String.format(
                    "An exception occurred while trying to load resource %s.",
                    key));
            return null;
        }
    }

    @Override
    public Enumeration<String> getKeys() {
        return (new Vector<String>(map.keySet())).elements();
    }

    @Override
    protected final Object handleGetObject(String key) {
        return this.loadImage(key, ".png");
    }

    private Image loadImage(String filename, String extension) {
        String imageName = filename + extension;

        Image image = map.get(imageName);

        if (image != null)
            return image;

        URL url = this.getClass().getResource("images/" + imageName);

        if (url == null)
            return null;

        image = Toolkit.getDefaultToolkit().createImage(url);
        map.put(imageName, image);

        return image;
    }
}
