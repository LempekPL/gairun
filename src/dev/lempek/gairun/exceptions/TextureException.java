package dev.lempek.gairun.exceptions;

import dev.lempek.gairun.enums.TextureEnum;
import dev.lempek.gairun.instances.Texture;

public class TextureException extends Exception {
    private TextureEnum err;

    public TextureException(TextureEnum err) {
        System.out.println(err);
        this.err = err;
    }

    public TextureException(TextureEnum err, String message) {
        System.out.println(err);
        System.out.println(message);
        this.err = err;
    }

    public TextureEnum getErr() {
        return err;
    }
}
