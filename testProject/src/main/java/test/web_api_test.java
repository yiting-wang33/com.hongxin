package test;

/**
 * @author yiting wang
 * @date 7/25/18 17:00
 * @email wang.yitin@husky.neu.edu
 */


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;



public class web_api_test {

    static int count;
    static String img_name;
    static FileWriter fw;
    static ArrayList<Integer> errPhoto;
    static FileWriter fwErr;

    /**
     * 发送HttpPost请求
     *
     * @param strURL
     *            服务地址
     * @param params
     *
     * @return 成功:返回json字符串<br/>
     */


    public static String sendPost(String strURL, String senddata) {
        try {
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
            out.append(senddata);
            out.flush();
            out.close();

            int code = connection.getResponseCode();
            InputStream is = null;
            if (code == 200) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }

            // 读取响应
            int length = (int) connection.getContentLength();// 获取长度
            if (length != -1) {
                byte[] data = new byte[length];
                byte[] temp = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(temp)) > 0) {
                    System.arraycopy(temp, 0, data, destPos, readLen);
                    destPos += readLen;
                }
                String result = new String(data, "UTF-8"); // utf-8编码
                return result;
            }

        } catch (IOException e) {
            System.out.println("Exception occur when send http post request!");
        }
        return "error"; // 自定义错误信息
    }


    public static void getJson(String path) throws Exception
    {   //调用api并输出单张图片的数据
        Base64Test base64_ = new Base64Test();
        String image_path1 ="/Users/yitinwang/Desktop/鸿信实习2018.07/kk_true/";
        image_path1 += path;
        //System.out.println(image_path1);

        String b64_1 = base64_.GetImageStr(image_path1);

        Map <String, String> params = new HashMap <String, String>();
        params.put("base64_str", b64_1);
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        params.put("img_id", uuid);


//    	String url ="http://127.0.0.1:5000/image_feature_api";
        String url ="http://202.102.101.217:58106/image_feature_api";
        JSONObject jsonObject = new JSONObject(params);

        String result =sendPost( url,  jsonObject.toString());
        String info = result.substring(9,result.length()-1); //对单张图片返回的Json格式数据
        //System.out.println(info);
        //取出result部分；
        JSONObject feature = new JSONObject(info);
        String data = feature.getString("result");
        String img_id = feature.getString("img_id");
        System.out.println(data);

        splitData(data,img_id);

    }


    public static void splitData(String data,String img_id) throws Exception {
        if (data.contains("*")) {

            System.out.println("图片"+count);
            System.out.println("结果大于1辆车！");
            fwErr.write("图片"+count+"\r");
            errPhoto.add(count);
            return;
        }

        String[] dataStr;

        String[] dataStr0 = data.split("\\|");
        System.out.println("图片"+count);
        System.out.println("特征值：" + dataStr0[0]);
        System.out.println("位置：" + dataStr0[1]);
        System.out.println("类型：" + dataStr0[2]);
        System.out.println("车牌位置：" + dataStr0[3]);
        System.out.println("车牌：" + dataStr0[4]);
        System.out.println("车型：" + dataStr0[5]);
        System.out.println("颜色：" + dataStr0[6]);
        System.out.println("品牌：" + dataStr0[7]);
        System.out.println("车标类型：" + dataStr0[8]);
        System.out.println("车标位置：" + dataStr0[9]);
        System.out.println();

        fw.write("图片"+count+" "+dataStr0[0]+" "+dataStr0[1]+" "+dataStr0[2]+" "+dataStr0[3]+" "
                +dataStr0[4]+" "+dataStr0[5]+" "+dataStr0[6]+" "+dataStr0[9]+" "+dataStr0[7]+"\r\n");

        vehicle vh = new vehicle();
        vh.setPhotoId(count);
        vh.setEigenvector(dataStr0[0]);
        vh.setPosition(dataStr0[1]);
        vh.setType(dataStr0[2]);
        vh.setLicense_position(dataStr0[3]);
        vh.setLicense(dataStr0[4]);
        vh.setModel(dataStr0[5]);
        vh.setColor(dataStr0[6]);
        vh.setLogo_position(dataStr0[9]);
        vh.setBrand(dataStr0[7]);

        insertResult(vh);

    }

    public static void insertResult(vehicle vh) throws Exception  {
        Connection conn=DBUtil.getConnection();

            String sql="" +
                    "insert into web_api_test" +
                    "(photoId,eigenvector,vh_position,vh_type,license_position,license,model,color,logo_position,brand)" +
                    "values(" +
                    "?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ptmt=conn.prepareStatement(sql);
            ptmt.setInt(1,vh.getPhotoId());
            ptmt.setString(2,vh.getEigenvector());
            ptmt.setString(3,vh.getPosition());
            ptmt.setString(4,vh.getType());
            ptmt.setString(5,vh.getLicense_position());
            ptmt.setString(6,vh.getLicense());
            ptmt.setString(7,vh.getModel());
            ptmt.setString(8,vh.getColor());
            ptmt.setString(9,vh.getLogo_position());
            ptmt.setString(10,vh.getBrand());
            ptmt.execute();

    }


    public static void main(String args[]) throws IOException {

        fw = new FileWriter("b.txt");
        fwErr = new FileWriter("err.txt");
        errPhoto = new ArrayList<Integer>();

        //String path = "/Users/yitinwang/Desktop/鸿信实习2018.07/kk_ture";
        // String names = "";
        count = 0;
        try {/**
            File f = new File(path);
            if (f.isDirectory())
            {
                File[] fList = f.listFiles();
                for (int j = 0; j < fList.length; j++) {
                    File file = fList[j];
                    if (file.isFile())
                    {
                        names += file.getName();
                        img_name = names;
                        count++;
                        System.out.println("图片"+count+"\r");
                        System.out.println("图片名称："+img_name+"\r");
                        fw.write("图片"+count);
                        fw.write("图片名称："+img_name);
                        getJson(names);
                        //System.out.println(names);
                        names = "";
                    }
                }
            }
         */
            for (count = 906;count<=976;count++) {
                //System.out.println("图片"+count);
                //fw.write("图片"+count+"\r");
                getJson(Integer.toString(count)+".jpeg");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("共计"+count+"张图片");
        System.out.print("有问题的图片ID:");
        for (int i: errPhoto) {
            System.out.print(i+" ");
        }
    }
}