/* Copyright (c) 2014 Varun Pant
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * original code retrieved from
 * https://github.com/varunpant/GHEAT-JAVA
 *
 * The code was modified by the IDSC-Frazzoli team at the
 * Institute for Dynamic Systems and Control of ETH Zurich 
 * for use in the amodeus library, 2017-2018. */

package ch.ethz.idsc.amodeus.view.gheat.gui;

enum Blender {
    ;
    public static BlendInterface of(BlendComposite composite) {
        switch (composite.getMode()) {
        case ADD:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = Math.min(255, src[0] + dst[0]);
                    result[1] = Math.min(255, src[1] + dst[1]);
                    result[2] = Math.min(255, src[2] + dst[2]);
                    result[3] = Math.min(255, src[3] + dst[3]);
                }
            };
        case AVERAGE:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = (src[0] + dst[0]) >> 1;
                    result[1] = (src[1] + dst[1]) >> 1;
                    result[2] = (src[2] + dst[2]) >> 1;
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case BLUE:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = dst[0];
                    result[1] = src[1];
                    result[2] = dst[2];
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case COLOR:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    float[] srcHSL = new float[3];
                    ColorUtilities.RGBtoHSL(src[0], src[1], src[2], srcHSL);
                    float[] dstHSL = new float[3];
                    ColorUtilities.RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);
                    ColorUtilities.HSLtoRGB(srcHSL[0], srcHSL[1], dstHSL[2], result);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case COLOR_BURN:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = src[0] == 0 ? 0 : Math.max(0, 255 - (((255 - dst[0]) << 8) / src[0]));
                    result[1] = src[1] == 0 ? 0 : Math.max(0, 255 - (((255 - dst[1]) << 8) / src[1]));
                    result[2] = src[2] == 0 ? 0 : Math.max(0, 255 - (((255 - dst[2]) << 8) / src[2]));
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case COLOR_DODGE:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = src[0] == 255 ? 255 : Math.min((dst[0] << 8) / (255 - src[0]), 255);
                    result[1] = src[1] == 255 ? 255 : Math.min((dst[1] << 8) / (255 - src[1]), 255);
                    result[2] = src[2] == 255 ? 255 : Math.min((dst[2] << 8) / (255 - src[2]), 255);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case DARKEN:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = Math.min(src[0], dst[0]);
                    result[1] = Math.min(src[1], dst[1]);
                    result[2] = Math.min(src[2], dst[2]);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case DIFFERENCE:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = Math.abs(dst[0] - src[0]);
                    result[1] = Math.abs(dst[1] - src[1]);
                    result[2] = Math.abs(dst[2] - src[2]);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case EXCLUSION:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = dst[0] + src[0] - (dst[0] * src[0] >> 7);
                    result[1] = dst[1] + src[1] - (dst[1] * src[1] >> 7);
                    result[2] = dst[2] + src[2] - (dst[2] * src[2] >> 7);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case FREEZE:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = src[0] == 0 ? 0 : Math.max(0, 255 - (255 - dst[0]) * (255 - dst[0]) / src[0]);
                    result[1] = src[1] == 0 ? 0 : Math.max(0, 255 - (255 - dst[1]) * (255 - dst[1]) / src[1]);
                    result[2] = src[2] == 0 ? 0 : Math.max(0, 255 - (255 - dst[2]) * (255 - dst[2]) / src[2]);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case GLOW:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = dst[0] == 255 ? 255 : Math.min(255, src[0] * src[0] / (255 - dst[0]));
                    result[1] = dst[1] == 255 ? 255 : Math.min(255, src[1] * src[1] / (255 - dst[1]));
                    result[2] = dst[2] == 255 ? 255 : Math.min(255, src[2] * src[2] / (255 - dst[2]));
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case GREEN:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = dst[0];
                    result[1] = dst[1];
                    result[2] = src[2];
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case HARD_LIGHT:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = src[0] < 128 ? dst[0] * src[0] >> 7 : 255 - ((255 - src[0]) * (255 - dst[0]) >> 7);
                    result[1] = src[1] < 128 ? dst[1] * src[1] >> 7 : 255 - ((255 - src[1]) * (255 - dst[1]) >> 7);
                    result[2] = src[2] < 128 ? dst[2] * src[2] >> 7 : 255 - ((255 - src[2]) * (255 - dst[2]) >> 7);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case HEAT:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = dst[0] == 0 ? 0 : Math.max(0, 255 - (255 - src[0]) * (255 - src[0]) / dst[0]);
                    result[1] = dst[1] == 0 ? 0 : Math.max(0, 255 - (255 - src[1]) * (255 - src[1]) / dst[1]);
                    result[2] = dst[2] == 0 ? 0 : Math.max(0, 255 - (255 - src[2]) * (255 - src[2]) / dst[2]);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case HUE:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    float[] srcHSL = new float[3];
                    ColorUtilities.RGBtoHSL(src[0], src[1], src[2], srcHSL);
                    float[] dstHSL = new float[3];
                    ColorUtilities.RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);
                    ColorUtilities.HSLtoRGB(srcHSL[0], dstHSL[1], dstHSL[2], result);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case INVERSE_COLOR_BURN:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = dst[0] == 0 ? 0 : Math.max(0, 255 - (((255 - src[0]) << 8) / dst[0]));
                    result[1] = dst[1] == 0 ? 0 : Math.max(0, 255 - (((255 - src[1]) << 8) / dst[1]));
                    result[2] = dst[2] == 0 ? 0 : Math.max(0, 255 - (((255 - src[2]) << 8) / dst[2]));
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case INVERSE_COLOR_DODGE:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = dst[0] == 255 ? 255 : Math.min((src[0] << 8) / (255 - dst[0]), 255);
                    result[1] = dst[1] == 255 ? 255 : Math.min((src[1] << 8) / (255 - dst[1]), 255);
                    result[2] = dst[2] == 255 ? 255 : Math.min((src[2] << 8) / (255 - dst[2]), 255);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case LIGHTEN:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = Math.max(src[0], dst[0]);
                    result[1] = Math.max(src[1], dst[1]);
                    result[2] = Math.max(src[2], dst[2]);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case LUMINOSITY:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    float[] srcHSL = new float[3];
                    ColorUtilities.RGBtoHSL(src[0], src[1], src[2], srcHSL);
                    float[] dstHSL = new float[3];
                    ColorUtilities.RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);
                    ColorUtilities.HSLtoRGB(dstHSL[0], dstHSL[1], srcHSL[2], result);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case MULTIPLY:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = (src[0] * dst[0]) >> 8;
                    result[1] = (src[1] * dst[1]) >> 8;
                    result[2] = (src[2] * dst[2]) >> 8;
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case NEGATION:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = 255 - Math.abs(255 - dst[0] - src[0]);
                    result[1] = 255 - Math.abs(255 - dst[1] - src[1]);
                    result[2] = 255 - Math.abs(255 - dst[2] - src[2]);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case OVERLAY:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = dst[0] < 128 ? dst[0] * src[0] >> 7 : 255 - ((255 - dst[0]) * (255 - src[0]) >> 7);
                    result[1] = dst[1] < 128 ? dst[1] * src[1] >> 7 : 255 - ((255 - dst[1]) * (255 - src[1]) >> 7);
                    result[2] = dst[2] < 128 ? dst[2] * src[2] >> 7 : 255 - ((255 - dst[2]) * (255 - src[2]) >> 7);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case RED:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = src[0];
                    result[1] = dst[1];
                    result[2] = dst[2];
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case REFLECT:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = src[0] == 255 ? 255 : Math.min(255, dst[0] * dst[0] / (255 - src[0]));
                    result[1] = src[1] == 255 ? 255 : Math.min(255, dst[1] * dst[1] / (255 - src[1]));
                    result[2] = src[2] == 255 ? 255 : Math.min(255, dst[2] * dst[2] / (255 - src[2]));
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case SATURATION:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    float[] srcHSL = new float[3];
                    ColorUtilities.RGBtoHSL(src[0], src[1], src[2], srcHSL);
                    float[] dstHSL = new float[3];
                    ColorUtilities.RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);
                    ColorUtilities.HSLtoRGB(dstHSL[0], srcHSL[1], dstHSL[2], result);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case SCREEN:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = 255 - ((255 - src[0]) * (255 - dst[0]) >> 8);
                    result[1] = 255 - ((255 - src[1]) * (255 - dst[1]) >> 8);
                    result[2] = 255 - ((255 - src[2]) * (255 - dst[2]) >> 8);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case SOFT_BURN:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = dst[0] + src[0] < 256 ? (dst[0] == 255 ? 255 : Math.min(255, (src[0] << 7) / (255 - dst[0]))) : Math.max(0, 255 - (((255 - dst[0]) << 7) / src[0]));
                    result[1] = dst[1] + src[1] < 256 ? (dst[1] == 255 ? 255 : Math.min(255, (src[1] << 7) / (255 - dst[1]))) : Math.max(0, 255 - (((255 - dst[1]) << 7) / src[1]));
                    result[2] = dst[2] + src[2] < 256 ? (dst[2] == 255 ? 255 : Math.min(255, (src[2] << 7) / (255 - dst[2]))) : Math.max(0, 255 - (((255 - dst[2]) << 7) / src[2]));
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case SOFT_DODGE:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = dst[0] + src[0] < 256 ? (src[0] == 255 ? 255 : Math.min(255, (dst[0] << 7) / (255 - src[0]))) : Math.max(0, 255 - (((255 - src[0]) << 7) / dst[0]));
                    result[1] = dst[1] + src[1] < 256 ? (src[1] == 255 ? 255 : Math.min(255, (dst[1] << 7) / (255 - src[1]))) : Math.max(0, 255 - (((255 - src[1]) << 7) / dst[1]));
                    result[2] = dst[2] + src[2] < 256 ? (src[2] == 255 ? 255 : Math.min(255, (dst[2] << 7) / (255 - src[2]))) : Math.max(0, 255 - (((255 - src[2]) << 7) / dst[2]));
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case SOFT_LIGHT:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    int mRed = src[0] * dst[0] / 255;
                    int mGreen = src[1] * dst[1] / 255;
                    int mBlue = src[2] * dst[2] / 255;
                    result[0] = mRed + src[0] * (255 - ((255 - src[0]) * (255 - dst[0]) / 255) - mRed) / 255;
                    result[1] = mGreen + src[1] * (255 - ((255 - src[1]) * (255 - dst[1]) / 255) - mGreen) / 255;
                    result[2] = mBlue + src[2] * (255 - ((255 - src[2]) * (255 - dst[2]) / 255) - mBlue) / 255;
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case STAMP:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = Math.max(0, Math.min(255, dst[0] + 2 * src[0] - 256));
                    result[1] = Math.max(0, Math.min(255, dst[1] + 2 * src[1] - 256));
                    result[2] = Math.max(0, Math.min(255, dst[2] + 2 * src[2] - 256));
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        case SUBTRACT:
            return new BlendInterface() {
                @Override
                public void blend(int[] src, int[] dst, int[] result) {
                    result[0] = Math.max(0, src[0] + dst[0] - 256);
                    result[1] = Math.max(0, src[1] + dst[1] - 256);
                    result[2] = Math.max(0, src[2] + dst[2] - 256);
                    result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                }
            };
        }
        throw new IllegalArgumentException("Blender not implemented for " + composite.getMode().name());
    }
}
