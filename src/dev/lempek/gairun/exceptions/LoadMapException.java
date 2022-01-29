package dev.lempek.gairun.exceptions;

import dev.lempek.gairun.enums.LoadMapEnum;
import dev.lempek.gairun.enums.TextureEnum;

public class LoadMapException extends Exception {
    private LoadMapEnum err;

    public LoadMapException(LoadMapEnum err) {
        System.out.println(err);
        this.err = err;
    }

    public LoadMapException(LoadMapEnum err, TextureEnum tEnum) {
        System.out.println(err);
        System.out.println(tEnum);
        this.err = err;
    }

    public LoadMapException(LoadMapEnum err, String message) {
        System.out.println(err);
        System.out.println(message);
        this.err = err;
    }

    public LoadMapEnum getErr() {
        return err;
    }
}
