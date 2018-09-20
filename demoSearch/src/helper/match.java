package helper;


import java.sql.*;
import java.util.*;

import testTarget.web_api.*;
/**
 * @author yiting wang
 * @date 7/30/18 15:18
 * @email wang.yitin@husky.neu.edu
 */
public class match {


    private static String photoId;
    private static double eucDis;
    private static Map<String, Double> map;

    /**
     *
     * @param f1
     * @param f2
     * @return 返回两个特征值的欧式距离
     */
    public static double eucDistance(String[] f1, String[] f2) {
        //计算出1000点之间的欧式距离
        //返回target和当前被测点的欧式距离
        Double euc = 0.00;
        for (int i = 1;i<f1.length;i++) {
            euc += Math.pow(Double.valueOf(f1[i]) - Double.valueOf(f2[i]),2);
        }
        return euc;
    }

    /**
     * 输入包含特征值的字符串 "1000_特征值1_特征值2_特征值3_……_特征值1000"
     * 转换为String[]数组
     *
     * @param st1
     * @param st2
     * @return euc 两张图片特征值的欧式距离
     */
    public static double calculate(String st1, String st2) {
        //取出相对应的1000个特征值
        String[] f1 = st1.split("_");
        String[] f2 = st2.split("_");
        Double euc = eucDistance(f1,f2);
        return euc;
    }

    /**
     * 计算被搜图片与db中每一张图片的特征值的欧式距离
     * @param ResultType
     *
     * @throws SQLException
     */
    public static void searchDB(ResultType rt) throws SQLException {
        Connection conn=DBUtil.getConnection();
        Statement stmt = conn.createStatement();
        map = new HashMap<String, Double>();
        //max = 0.0;
        /**
        String sql = "select photoId,eigenvector from web_api_test where model = ? and color = ?";
        PreparedStatement ptmt=conn.prepareStatement(sql);
        ptmt.setString(1,rt.model);
        ptmt.setString(2,rt.color);
        ResultSet rs = ptmt.executeQuery();*/
        ResultSet rs = stmt.executeQuery("select photoId,eigenvector,license,color from web_api_test");
        System.out.println("被搜车辆信息："+rt.license+" "+rt.color+" "+rt.model);
        while (rs.next()) {
            //System.out.println(rs.getString("eigenvector"));
            photoId = Integer.toString(rs.getInt("photoId"));
            double mat = getMat(rt,rs.getString("eigenvector"),rs.getString("color"),rs.getString("license"));
            //eucDis = calculate(rs.getString("eigenvector"),rt.tarEigen);
            //map.put(photoId,eucDis);
            map.put(photoId,mat);
            //System.out.println(photoId+" "+eucDis);
        }

        //map从小到大排序

        ByValueComparator bvc = new ByValueComparator(map);
        ArrayList<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys, bvc);
        for (int i = 0;i<5;i++) {
            System.out.println(keys.get(i)+" "+map.get(keys.get(i)));
        }



        //System.out.println(max);
    }

    public static double getMat(ResultType rt, String eigenvector, String color,String license)//,double features2[]
    {
        double threshold = 2.6;
        double Q,Qmax,mat;
        //Q为匹配度；Qmax为最大匹配度，用于归一化；mat为相似度
        double q,DIS;
        //q为color和license的匹配度，DIS为欧氏距离
        double p1 = 0.94;//颜色识别正确率
        double p2 = 0.85;//车牌识别正确率
        int DISmax = 7200;//最大欧氏距离
        int temp1 = (rt.color.equals(color)?2:1);
        int temp2 = (rt.license.equals(license)?2:1);
        q = p1*temp1+p2*temp2;
        //System.out.println(rt.color+" "+rt.license);
        //System.out.println(temp1+" "+temp2);
        //System.out.println(q);
        if(q > threshold) {
            DIS = calculate(eigenvector,rt.tarEigen);//distance算欧氏距离
            Q = q + 2*(1-DIS/DISmax);
            Qmax = 2*(p1+p2+1);
            mat = Q*100/Qmax;
            return mat;
        }else {
            return 0;
        }

    }


}
