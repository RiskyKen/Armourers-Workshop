package moe.plushie.armourers_workshop.common.capability.wardrobe;

import java.util.Arrays;

import moe.plushie.armourers_workshop.common.painting.PaintingHelper;

public class ExtraColours {

    public static final int COLOUR_NONE = 0x00000000;

    public static final ExtraColours EMPTY_COLOUR;
    static {
        EMPTY_COLOUR = new ExtraColours();
        for (int i = 0; i < ExtraColourType.values().length; i++) {
            EMPTY_COLOUR.setColour(ExtraColourType.values()[i], 0x00000000);
        }
    }

    public final int[] extraColoursArray;

    public ExtraColours(ExtraColours extraColours) {
        extraColoursArray = new int[ExtraColourType.values().length];
        for (int i = 0; i < ExtraColourType.values().length; i++) {
            setColour(ExtraColourType.values()[i], extraColours.getColour(ExtraColourType.values()[i]));
        }
    }

    public ExtraColours() {
        extraColoursArray = new int[ExtraColourType.values().length];
        for (ExtraColourType type : ExtraColourType.values()) {
            setColour(type, COLOUR_NONE);
        }
    }

    public int getColour(ExtraColourType type) {
        return extraColoursArray[type.ordinal()];
    }

    public byte[] getColourBytes(ExtraColourType type) {
        return PaintingHelper.intToBytes(extraColoursArray[type.ordinal()]);
    }

    public void setColour(ExtraColourType type, int trgb) {
        extraColoursArray[type.ordinal()] = trgb;
    }

    public void setColourBytes(ExtraColourType type, byte[] rgbt) {
        extraColoursArray[type.ordinal()] = PaintingHelper.bytesToInt(rgbt);
    }

    @Override
    public int hashCode() {
        final int prime = 193;
        return prime * Arrays.hashCode(extraColoursArray);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExtraColours other = (ExtraColours) obj;
        if (!Arrays.equals(extraColoursArray, other.extraColoursArray))
            return false;
        return true;
    }

    public enum ExtraColourType {
        SKIN, HAIR, EYE, MISC_1, MISC_2, MISC_3, MISC_4;
    }
}
