package rocketmq.flink;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author: zhangtiancheng
 * @Date: 2019-07-05
 */
public class CommonUtils {

    public static int rgb2box(int[] rgbArr){
        if (rgbArr == null) {
            return -1;
        }
        String rgbHex = rgbArr2hex(rgbArr);

        switch (rgbHex) {
            case "ffd700": return 101;
            case "ffa489": return 102;
            case "1b5633": return 103;
            case "fa364a": return 104;
            case "532a45": return 105;
            case "badce6": return 106;
            case "718473": return 107;
            case "ff5200": return 201;
            case "bd302c": return 202;
            case "0071ce": return 203;
            case "f196ce": return 204;
            case "410099": return 205;
            case "004d43": return 206;
            case "dcb48e": return 207;
            case "bcbcbc": return 301;
            case "7a6569": return 302;
            case "64513d": return 303;
            case "f4e95d": return 304;
            case "81bc00": return 305;
            case "385dae": return 306;
            case "653334": return 307;
            case "ff647e": return 308;
            case "00205c": return 309;
            case "4bc1e1": return 310;
            case "006853": return 311;
            case "f4bf9b": return 312;
            case "d3461e": return 313;
            case "d41568": return 314;
            case "d064ce": return 315;
            case "0033a1": return 316;
            case "006e97": return 317;
            case "008476": return 318;
            case "e1261c": return 319;
            case "ff828a": return 320;
            case "c64c38": return 321;
            case "f8b6cd": return 401;
            case "8e1537": return 402;
            case "ab8a00": return 403;
            case "ffb700": return 404;
            case "f0a055": return 405;
            case "633d21": return 406;
            case "7e57c6": return 407;
            default: return -1;
        }
    }

    private static String rgbArr2hex(int [] rgbArr){
        int r = rgbArr[0];
        int g = rgbArr[1];
        int b = rgbArr[2];

        String rs = Integer.toHexString(r);
        String gs = Integer.toHexString(g);
        String bs = Integer.toHexString(b);

        // 如果是一位数就在前面补0
        rs = rs.length() == 1 ? "0" + rs : rs;
        gs = gs.length() == 1 ? "0" + gs : gs;
        bs = bs.length() == 1 ? "0" + bs : bs;

        String res = rs + gs + bs;
        return res;
    }


    public static int[]  getRGB(BufferedImage image,int x,int y){
        Color color = new Color(image.getRGB(x, y),false);
        int[] rgb = new  int  [3];
        rgb[0]=color.getRed();
        rgb[1]=color.getGreen();
        rgb[2]=color.getBlue();
        return rgb;
    }



    /**
     * 根据图片的像素坐标获取相应的rgb值并匹配对应的Bettingbox编号
     * @param image
     * @param x
     * @param y
     * @return
     */
    public static int positionToBoxNum(BufferedImage image, int x, int y){
        return  rgb2box(getRGB(image,x,y));
    }



    public static void main(String[] args) {

        int[] rgbArr = new int[3];
        rgbArr[0] = 255;
        rgbArr[1] = 82;
        rgbArr[2] = 0;

        int i = rgb2box(rgbArr);
        System.out.println(i);

    }





}
