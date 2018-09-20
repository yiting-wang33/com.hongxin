package testSingleCar;

/**
 * @author yiting wang
 * @date 7/27/18 10:11
 * @email wang.yitin@husky.neu.edu
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;



public class web_api_test_single {



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


    public static void main(String args[]) throws IOException
    {
        Base64Test base64_ = new Base64Test();
        String image_path1 ="/Users/yitinwang/Desktop/鸿信实习2018.07/kk/504.jpeg";
        String b64_1 = base64_.GetImageStr(image_path1);

        Map <String, String> params = new HashMap <String, String>();
        params.put("base64_str", b64_1);
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        params.put("img_id", uuid);


//    	String url ="http://127.0.0.1:5000/image_feature_api";
        String url ="http://202.102.101.217:58106/image_feature_api";
        JSONObject jsonObject = new JSONObject(params);

        String result =sendPost( url,  jsonObject.toString());
        System.out.println(result);

        String info = result.substring(9,result.length()-1); //对单张图片返回的Json格式数据
        //System.out.println(info);
        //取出result部分；
        JSONObject feature = new JSONObject(info);
        String data = feature.getString("result");
        String img_id = feature.getString("img_id");

        System.out.println(data);

        String[] dataStr;
        dataStr = data.split("\\*",-1);
        int i = 0;
        //信息为空的车直接不输出
        while (dataStr[i] == null) i++;
        int size = dataStr.length;//识别出图中车的数量；
        //输出第一辆识别信息不为空的车：
        //System.out.println("图片"+count);
        //System.out.println("图片名称："+img_name);
        System.out.println("dataStr[i]:"+dataStr[i]);
        String[] dataStr0 = dataStr[i].split("\\|");
        System.out.println("车辆个数：" + size);
        System.out.println("第"+(i+1)+"车的识别信息为：");
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


    }
}