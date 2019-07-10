package rocketmq.flink;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class PictureTest {

    public static void main(String[] args) {

        try {
            BufferedImage image = ImageIO.read(new FileInputStream("src/main/resources/birdview.png"));

           // Color color = new Color(image.getRGB(69, 17),false);
            //Color color = new Color(image.getRGB(2039, 1073),false);


           // Color color = new Color(image.getRGB(2043, 1077),false);

            Color color = new Color(image.getRGB(1853, 264),false);


            System.out.println(color.getRed()+"  "+color.getGreen()+ "  "+color.getBlue());

            //System.out.println(CommonUtils.positionToBoxNum(image,1853, 264));

            //int[] rgb = getRGB(image, 20, 12); //168 112 25
            //int[] rgb = getRGB(image, 21, 13); //168 112 25

             //int[] rgb = getRGB(image, 70, 16); //169 114 30 #A9721E
            //int[] rgb = getRGB(image, 71, 18); //170 114 36

         /*   int[] rgb = getRGB(image, 69, 17); //170 114 36

            Color color = new Color(rgb[0], rgb[1], rgb[2]++);
            System.out.println("red = " + color.getRed()+" one: "+rgb[0]+"==="+image.getMinX()+image.getMinY());
            System.out.println("green = " + color.getGreen()+"  two: "+rgb[1]);
            System.out.println("blue = " + color.getBlue()+" three: "+rgb[2]);
*/




        } catch (IOException e) {
            e.printStackTrace();
        }

    }


        public static int[]  getRGB(BufferedImage image,int x,int y){
            Color color = new Color(image.getRGB(x, y),false);
            int[] rgb = new  int  [3];
            rgb[0]=color.getRed();
            rgb[1]=color.getGreen();
            rgb[2]=color.getBlue();
           return rgb;
        }



    /*
              *//**
          *  取得图像上指定位置像素的  rgb  颜色分量。
          *  @param  image  源图像。
          *  @param  x  图像上指定像素位置的  x  坐标。
          *  @param  y  图像上指定像素位置的  y  坐标。
          *  @return  返回包含  rgb  颜色分量值的数组。元素  index  由小到大分别对应  r，g，b。
          *//*
            public static int[] getRGB(BufferedImage image, int x, int y){
                int[] rgb = new  int  [3];
                int  pixel  =  image.getRGB(x,  y);
                rgb[0]  =  (pixel  &  0xff0000)  >>  16;
                rgb[1]  =  (pixel  &  0xff00)  >>  8;
                rgb[2]  =  (pixel  &  0xff);

                return rgb;
            }*/

    /**
     * 将RGB转换为16进制Hex
     *
     * @param r red颜色分量
     * @param g green颜色分量
     * @param b blue颜色分量
     * @return
     */
    public static String toHex(int r, int g, int b) {
        return "#" + toHexValue(r) + toHexValue(g) + toHexValue(b);
    }

    private static String toHexValue(int number) {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }



}
