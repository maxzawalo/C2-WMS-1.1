package maxzawalo.c2.full.data.json.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Logger;

public class CurrencyFromNet {

	protected static Logger log = Logger.getLogger(CurrencyFromNet.class);

	public static void Load() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();

		Date startDate = Format.GetDate("01.07.2016");
		Date endDate = Format.AddMonth(startDate, 1);

		while (startDate.getTime() <= new Date().getTime()) {

			// http://www.nbrb.by/API/ExRates/Rates/RUB?ParamMode=2&onDate=2017-05-05
			// http://www.nbrb.by/API/ExRates/Rates/Dynamics/298?startDate=2017-05-01&endDate=2017-06-01

			// 01.07.2016
			String data = "";// "[{\"Cur_ID\":298,\"Date\":\"2017-05-01T00:00:00\",\"Cur_OfficialRate\":3.2803},{\"Cur_ID\":298,\"Date\":\"2017-05-02T00:00:00\",\"Cur_OfficialRate\":3.2803},{\"Cur_ID\":298,\"Date\":\"2017-05-03T00:00:00\",\"Cur_OfficialRate\":3.2804},{\"Cur_ID\":298,\"Date\":\"2017-05-04T00:00:00\",\"Cur_OfficialRate\":3.2775},{\"Cur_ID\":298,\"Date\":\"2017-05-05T00:00:00\",\"Cur_OfficialRate\":3.2717},{\"Cur_ID\":298,\"Date\":\"2017-05-06T00:00:00\",\"Cur_OfficialRate\":3.2381},{\"Cur_ID\":298,\"Date\":\"2017-05-07T00:00:00\",\"Cur_OfficialRate\":3.2481},{\"Cur_ID\":298,\"Date\":\"2017-05-08T00:00:00\",\"Cur_OfficialRate\":3.2481},{\"Cur_ID\":298,\"Date\":\"2017-05-09T00:00:00\",\"Cur_OfficialRate\":3.2481},{\"Cur_ID\":298,\"Date\":\"2017-05-10T00:00:00\",\"Cur_OfficialRate\":3.2481},{\"Cur_ID\":298,\"Date\":\"2017-05-11T00:00:00\",\"Cur_OfficialRate\":3.2468},{\"Cur_ID\":298,\"Date\":\"2017-05-12T00:00:00\",\"Cur_OfficialRate\":3.2767},{\"Cur_ID\":298,\"Date\":\"2017-05-13T00:00:00\",\"Cur_OfficialRate\":3.2749},{\"Cur_ID\":298,\"Date\":\"2017-05-14T00:00:00\",\"Cur_OfficialRate\":3.2749},{\"Cur_ID\":298,\"Date\":\"2017-05-15T00:00:00\",\"Cur_OfficialRate\":3.2749},{\"Cur_ID\":298,\"Date\":\"2017-05-16T00:00:00\",\"Cur_OfficialRate\":3.2915},{\"Cur_ID\":298,\"Date\":\"2017-05-17T00:00:00\",\"Cur_OfficialRate\":3.2928},{\"Cur_ID\":298,\"Date\":\"2017-05-18T00:00:00\",\"Cur_OfficialRate\":3.2758},{\"Cur_ID\":298,\"Date\":\"2017-05-19T00:00:00\",\"Cur_OfficialRate\":3.2485},{\"Cur_ID\":298,\"Date\":\"2017-05-20T00:00:00\",\"Cur_OfficialRate\":3.2588},{\"Cur_ID\":298,\"Date\":\"2017-05-21T00:00:00\",\"Cur_OfficialRate\":3.2588},{\"Cur_ID\":298,\"Date\":\"2017-05-22T00:00:00\",\"Cur_OfficialRate\":3.2588}]";
			String url = "http://www.nbrb.by/API/ExRates/Rates/Dynamics/298?startDate="
					+ Format.Show("yyyy-MM-dd", startDate) + "&endDate=" + Format.Show("yyyy-MM-dd", endDate);

			try {
				URLConnection conn = new URL(url).openConnection();
				// open the stream and put it into BufferedReader
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

				String inputLine;
				while ((inputLine = br.readLine()) != null) {
					data += inputLine;
				}
				br.close();

				String rates = "";
				List list = (List) gson.fromJson(data, Object.class);// ReadBONullable
				for (Object obj : list) {
					LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) obj;
					double rate = Double.parseDouble("" + map.get("Cur_OfficialRate"));
					double scale = 100;// Double.parseDouble("" +
										// map.get("Cur_Scale"));
					Date date = Format.extractJsonDate("" + map.get("Date"));
					rates += Format.Show(date) + "\t" + rate + "\t" + scale + "\r\n";
				}

				FileUtils.Text2File(FileUtils.GetDataDir() + "RUB", rates, true);

				startDate = Format.AddMonth(startDate, 1);
				endDate = Format.AddMonth(startDate, 1);

			} catch (Exception e) {
				log.ERROR("Load", e);
				Console.I().ERROR(CurrencyFromNet.class, "Load", "Ошибка загрузки курсов валют. См. лог.");
				return;
			}
		}
	}
}