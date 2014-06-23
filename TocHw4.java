import java.io.*;
import java.net.*;
import java.sql.Struct;
import java.util.*;
import org.json.*;

/*	parse url json from 內政部房屋交易
 * 	record deal month of each area
 * 	and find where have the most month of deal
 */
class area_price{
	public int max,min;
	public area_price(){
		max=0;
		min=1000000000;
	}
};

public class TocHw4 {
	/**
	 * @param args
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws JSONException, UnsupportedEncodingException, IOException {
		// TODO 自動產生的方法 Stub
		int road_num=2000,id=0;	//road_num is array of size, id is record id of the road
		URL url =new URL(args[0]);
		URLConnection connect = url.openConnection();
		InputStreamReader isr = new InputStreamReader(connect.getInputStream(), "UTF-8");
		JSONArray data = new JSONArray(new JSONTokener(isr));
		HashMap<String,Integer> road_id=new HashMap<String,Integer>();	//ex road_id(臺北市文山區辛亥路)=1
		HashMap<Integer,String> id_road=new HashMap<Integer,String>();	// ex road_id(1)=臺北市文山區辛亥路
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] road_month =new ArrayList[road_num];	//push diff month to each road
		for(int i=0;i<road_num;i++)	road_month[i]=new ArrayList<Integer>();
		area_price[] area=new area_price[road_num];	//record max and min price of each month 
		for(int i=0;i<road_num;i++)	area[i]=new area_price();
		int len=data.length();
		for(int i=0;i<len;i++){
			JSONObject tmp=data.getJSONObject(i);
			String s=tmp.getString("土地區段位置或建物區門牌");
			int j;
			for(j=0;j<s.length();j++){	//cut the string
				if(s.charAt(j)=='路' || s.charAt(j)=='街' || s.charAt(j)=='巷'
						|| (j>0&&s.charAt(j-1)=='大'&&s.charAt(j)=='道')){
					s=s.substring(0, j+1);
					break;
				}
			}
			j=s.length()-1;
			if(s.charAt(j)!='路' && s.charAt(j)!='街' && s.charAt(j)!='巷' && s.charAt(j)!='道')	continue;
			if(road_id.containsKey(s)==false){	//if the road is not appear before
				road_id.put(s, id);				//new id for it
				id_road.put(id++,s);
			}
			int rid=road_id.get(s),month=tmp.getInt("交易年月"),price=tmp.getInt("總價元");
			if(road_month[rid].contains(month)==false){	//if this road not have this month
				road_month[rid].add(month);
			}
			if(area[rid].max<price)	area[rid].max=price;	//check this max and min of this road
			if(area[rid].min>price) area[rid].min=price;
		}
		ArrayList<Integer> ans=new ArrayList<Integer>();	//find and print ans
		int maxnum=0;
		for(int i=0;i<id;i++){
			if(road_month[i].size()>maxnum){	//change ans
				maxnum=road_month[i].size();
				ans.clear();
				ans.add(i);
			}else if(road_month[i].size()==maxnum){	//have not only ans 
				ans.add(i);
			}
		}
		len=ans.size();
		for(int i=0;i<len;i++)	//print ans
			System.out.println(id_road.get(ans.get(i))+", 最高成交價: "
				+area[ans.get(i)].max+", 最低成交價: "+area[ans.get(i)].min);
	}

}
