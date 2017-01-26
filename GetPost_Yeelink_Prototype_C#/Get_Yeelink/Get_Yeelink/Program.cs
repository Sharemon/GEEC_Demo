using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;

namespace Get_Yeelink
{
    class Program
    {
        static void Main(string[] args)
        {
            int count = 0;
            while (true)
            {
                // If want to implement Http GET, comment some lines and region(POST json) and uncomment some lines

                #region Post Temperature
                Uri mUri = new Uri("http://api.yeelink.net/v1.1/device/354183/sensor/399924/datapoints");
                HttpWebRequest mRequest = (HttpWebRequest)WebRequest.Create(mUri);
                mRequest.Timeout = 20000;
                //mRequest.Method = "GET";
                mRequest.Method = "POST";

                mRequest.Headers.Add("U-ApiKey", " 53b215fba5cf7657889a5180890ddb82");

                #region POST json
                // Randomize a value from 20 to 30
                System.Random mRand = new Random();
                double value = mRand.NextDouble() * 10 + 20;

                StreamWriter writer = new StreamWriter(mRequest.GetRequestStream(), Encoding.ASCII);
                writer.Write("{\"value\":" + value.ToString("0.00") + "}");
                writer.Flush();
                #endregion


                HttpWebResponse httpResponse = (HttpWebResponse)mRequest.GetResponse();
                StreamReader sr = new StreamReader(httpResponse.GetResponseStream(), System.Text.Encoding.GetEncoding("gb2312"));
                string result = sr.ReadToEnd();
                int status = (int)httpResponse.StatusCode;
                sr.Close();

                Console.WriteLine(status.ToString());
                Console.WriteLine(result);

                count++;
                Console.WriteLine("第" + count.ToString() + "次读取数据\r\n");

                //System.Threading.Thread.Sleep(11000);
                //Console.WriteLine("press any key...");
                //Console.Read();
                #endregion


                #region Post Power
                mUri = new Uri("http://api.yeelink.net/v1.1/device/353959/sensor/399413/datapoints");
                mRequest = (HttpWebRequest)WebRequest.Create(mUri);
                mRequest.Timeout = 20000;
                //mRequest.Method = "GET";
                mRequest.Method = "POST";

                mRequest.Headers.Add("U-ApiKey", " 53b215fba5cf7657889a5180890ddb82");

                #region POST json
                // Randomize a value from 20 to 30
                mRand = new Random();
                value = mRand.NextDouble() * 10 + 45;

                writer = new StreamWriter(mRequest.GetRequestStream(), Encoding.ASCII);
                writer.Write("{\"value\":" + value.ToString("0.00")+ "}");
                writer.Flush();
                #endregion


                httpResponse = (HttpWebResponse)mRequest.GetResponse();
                sr = new StreamReader(httpResponse.GetResponseStream(), System.Text.Encoding.GetEncoding("gb2312"));
                result = sr.ReadToEnd();
                status = (int)httpResponse.StatusCode;
                sr.Close();

                Console.WriteLine(status.ToString());
                Console.WriteLine(result);

                count++;
                Console.WriteLine("第" + count.ToString() + "次读取数据\r\n");

                System.Threading.Thread.Sleep(11000);
                //Console.WriteLine("press any key...");
                //Console.Read();
                #endregion
            }
        }
    }
}
