package com.example.open_data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;







import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener
{
	private TextView tv_Location;
	private TextView tv_OpenData;
	private ListView listView;
	private boolean getService =false;//是否已開啟定位服務
	private  LocationManager lms;
	private  Location location;
	private String bestProvider=LocationManager.GPS_PROVIDER;
	private Double longitude,latitude;
	String s1,s2,s4;
	int s3;
	ListAdapter laMuilt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv_Location=(TextView)findViewById(R.id.tv_location);
		tv_OpenData=(TextView)findViewById(R.id.tv_json);
		listView=(ListView)findViewById(R.id.listView);
	//	new NewTaipeiHttpAsynoTask().execute("http://data.ntpc.gov.tw/NTPC/od/data/api/1040400255/?$format=json");//新北市
	//http://data.taipei.gov.tw/opendata/apply/query/MzVERDUyOTItNjI1NC00NjcyLUE3OEItNDY3ODhDMURFM0Yy?$format=json
		//new HttpAsynoTask().execute("http://data.taipei.gov.tw/opendata/apply/query/MzVERDUyOTItNjI1NC00NjcyLUE3OEItNDY3ODhDMURFM0Yy?$format=json");//台北市
		//取得系統定位服務
		//http://www.ca.taipei.gov.tw/ct.asp?xItem=1071040&CtNode=40544&mp=102001 台北所有區
				LocationManager status=(LocationManager)(this.getSystemService(Context.LOCATION_SERVICE));
				if(status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
				{
					//如果GPS或網路定位開啟,呼叫locationServiceInitial()更新位置
					locationServiceInitial();
				}
				else
				{
					Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
					getService=true;//確定開啟定位服務
					startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				}
				//new LocationAsynoTask().execute("http://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&sensor=false&language=zh-tw");
//				new HttpAsynoTask().execute("http://maps.googleapis.com/maps/api/geocode/json?latlng=25.047908,121.517315&sensor=false&language=zh-tw");
				new 	LocationAsynoTask().execute("http://maps.googleapis.com/maps/api/geocode/json?latlng=25.1836779,121.46377&sensor=false&language=zh-tw");
				//22.470683,114.274301
				//  25°11'1"N   121°24'44
				
	
	
		}
	private  class LocationAsynoTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected void onPostExecute(String result)
		{
		     try 
		    {
		    	   
		    	 	JSONArray jsonObjs =  new  JSONObject(result).getJSONArray( "results" );  
		            //取出數組中第一個json對象(本示例數組中實際只包含一個元素)  
		            JSONObject jsonObj = jsonObjs.getJSONObject( 0 );  
		            //解析得formatted_address值  
		            String address = jsonObj.getString( "formatted_address" ); //247台灣新北市蘆洲區三民路42號
		            
		           // s1=address.substring(5, 8);//新北市
		          //  s2=address.substring(8, 11);//新北市
			        s3=address.indexOf("台");//3
			        
		            s1=address.substring(s3+2, s3+5);//新北市
		            s2=address.substring(s3+5, s3+8);//蘆洲區
		            
		           // tv_Location.setText(s1+":"+s2+":"+s3);
		           // tv_Location.setText(address);//s1:新北市,s2:蘆洲區,s3抓取台這個位置
		    		Toast.makeText(getBaseContext(), address, Toast.LENGTH_LONG).show();
					
		            if(s1.equals("新北市")) 
					{
							Toast.makeText(getBaseContext(), "你現在位置在新北市內", Toast.LENGTH_LONG).show();
							new NewTaipeiHttpAsynoTask().execute("http://data.ntpc.gov.tw/NTPC/od/data/api/1040400255/?$format=json");//新北市
						
					}
					else
					{
						
						Toast.makeText(getBaseContext(), "你現在位置在台北市內", Toast.LENGTH_LONG).show();//http://data.taipei.gov.tw/opendata/apply/NewDataContent?oid=35DD5292-6254-4672-A78B-46788C1DE3F2
						new NewTaipeiHttpAsynoTask().execute("http://data.ntpc.gov.tw/NTPC/od/data/api/1040400255/?$format=json");//新北市
						
					}
		   
		    }
		     
		     catch (JSONException e) 
		     {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

		@Override
		protected String doInBackground(String...urls) 
		{
			// TODO Auto-generated method stub
			return  GET(urls[0]);
		}

		private String GET(String url) 
		{
			InputStream inputStream=null;
			String result="";
			try 
			{
				HttpClient httpclient=new DefaultHttpClient();
				HttpResponse httpResponse=httpclient.execute(new HttpGet(url));
				inputStream=httpResponse.getEntity().getContent();
				if(inputStream!=null)
				{
					result=covertInputStreamToString(inputStream);
				}
				else
				{
					result="Did not work";
				}
			} 
			catch (ClientProtocolException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
			return result;
		}

		private String covertInputStreamToString(InputStream inputStream) throws IOException 
		{
			BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
			String line="";
			String result="";
			// TODO Auto-generated method stub
			while((line=bufferedReader.readLine())!=null)
			{
				result+=line;
			}
			return result;
		}
		
		
	}
	
	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		if(getService)
		{
			lms.requestLocationUpdates(bestProvider, 1000, 1, this);
		}
	}

	@Override
	protected void onPause() 
	{
		// TODO Auto-generated method stub
		super.onPause();
		if(getService)
		{
			lms.removeUpdates(this);
		}
	}

	private void locationServiceInitial() 
	{
		// TODO Auto-generated method stub
		lms=(LocationManager)getSystemService(LOCATION_SERVICE);//取得系統定位服務
		Criteria criteria=new Criteria();
		bestProvider=lms.getBestProvider(criteria, true);
		Location location=lms.getLastKnownLocation(bestProvider);
		getLocation(location);
		
	}
	private void getLocation(Location location) 
	{
		// TODO Auto-generated method stub
		if(location!=null)
		{
				longitude=location.getLongitude();
				latitude=location.getLatitude();
		}
		else
		{
			Toast.makeText(this, "無法定位服務",Toast.LENGTH_LONG).show();
		}
		
	}
	private  class NewTaipeiHttpAsynoTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... urls)
		{
			// TODO Auto-generated method stub
			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) 
		{
			
			//Banqiao 板橋,Taishan 泰山, Sanchong三重,Linkou 林口,Chungho 中和 ,Bali 八里, Yungho 永和, Shenkeng 深坑, Hsinchuang 新莊 , Shihting 石碇,Xindian 新店 , Pinglin  坪林,Tucheng  土城
			//三芝   Sanchih,蘆洲 Luchou,石門 Shihmen,汐止  Hsichih,金山  Jinshan,樹林 Shulin ,萬里 WanLi,鶯歌 Yingko,平溪 Pinghsi ,三峽    Changjiang ,雙溪 ] Shuanghsi ,淡水   Tanshui, 貢寮  Kungliao , 瑞芳  Juifang , 烏來   Wulai 
			//五股 Wugu
			
		
			int count=0;
			 try 
			 {
				JSONArray jsonArray = new JSONArray(result);
				String address="";
				
				ArrayList<String>Banqiao=new ArrayList<String>();
				ArrayList<String>Taishan=new ArrayList<String>();
				ArrayList<String>Sanchong=new ArrayList<String>();
				ArrayList<String>Linkou=new ArrayList<String>();
				ArrayList<String>Chungho=new ArrayList<String>();
				ArrayList<String>Bali=new ArrayList<String>();
				ArrayList<String> Yungho=new ArrayList<String>();
				ArrayList<String>Shenkeng=new ArrayList<String>();
				ArrayList<String>Hsinchuang=new ArrayList<String>();
				ArrayList<String>Shihting=new ArrayList<String>();
				ArrayList<String>Xindian=new ArrayList<String>();
				ArrayList<String>Pinglin=new ArrayList<String>();
				ArrayList<String>Tucheng =new ArrayList<String>();
				ArrayList<String>Sanchih=new ArrayList<String>();
				ArrayList<String>Luchou=new ArrayList<String>();
				ArrayList<String>Shihmen=new ArrayList<String>();
				ArrayList<String>Hsichih=new ArrayList<String>();
				ArrayList<String>Jinshan=new ArrayList<String>();
				ArrayList<String>Shulin=new ArrayList<String>();
				ArrayList<String>WanLi=new ArrayList<String>();
				ArrayList<String>Yingko=new ArrayList<String>();
				ArrayList<String>Pinghsi=new ArrayList<String>();
				ArrayList<String>Changjiang=new ArrayList<String>();
				ArrayList<String>Shuanghsi =new ArrayList<String>();
				ArrayList<String>Tanshui=new ArrayList<String>();	
				ArrayList<String>Kungliao=new ArrayList<String>();
				ArrayList<String> Juifang=new ArrayList<String>();
				ArrayList<String>Wulai=new ArrayList<String>();
				ArrayList<String> Wugu=new ArrayList<String>();
				
				
				
				
				
				
				
				String []Part_add=new String [jsonArray.length()];
				String []Full_add=new String [jsonArray.length()];
				String []Full_Name=new String [jsonArray.length()];
				
				String Json_String = "";
				
				for (int i = 0; i <jsonArray.length(); i++) 
				{
		            JSONObject jsonObject = jsonArray.getJSONObject(i);
		            String Add = jsonObject.getString("Add");
		            String Name=jsonObject.getString("Name");
		            Part_add[i]=Add;//部分的Add(被切割區的部分)
		            Full_add[i]=Add;//完整的Add
		            Full_Name[i]=Name;
		            Log.i("Entry", "name: " + Add );
		         //   Toast.makeText(getApplicationContext(), Add,Toast.LENGTH_SHORT).show();
		            StringBuffer sb=new StringBuffer(Add);
		            //All_add[i]=sb.toString();
		       //     Toast.makeText(getApplicationContext(), All_add[i], Toast.LENGTH_SHORT).show();
		            // count++;
		            Json_String= sb.toString();
		            if(Json_String.length()>6)
		            {
		            	Part_add[i]=Json_String.substring(3, 6);
		            	 Log.i("Over6", "name: " +Part_add[i] );
				         // 	
		            }else//有些Add沒有詳細資料 只有六個字
		            {
		            	Part_add[i]="";
		           	 //Log.i("less6", "name: " + All_add[i] );
		           	 //Toast.makeText(getApplicationContext(), All_add[i]+":", Toast.LENGTH_SHORT).show();
					    
		            }
		            
		        	
					//Banqiao 板橋,Taishan 泰山, Sanchong三重,Linkou 林口,Chungho 中和 ,Bali 八里, Yungho 永和, Shenkeng 深坑, Hsinchuang 新莊 , Shihting 石碇,Xindian 新店 , Pinglin  坪林,Tucheng  土城
					//三芝   Sanchih,蘆洲 Luchou,石門 Shihmen,汐止  Hsichih,金山  Jinshan,樹林 Shulin ,萬里 WanLi,鶯歌 Yingko,平溪 Pinghsi ,三峽    Changjiang ,雙溪  Shuanghsi ,淡水   Tanshui, 貢寮  Kungliao , 瑞芳  Juifang , 烏來   Wulai 
					//五股 Wugu
					
		           switch(Part_add[i])
		           {
		           		case "板橋區":
		           			Banqiao.add(Full_Name[i]);
		           		break;	
		        
		           		case "泰山區":
		           			Taishan.add(Full_Name[i]);
		           		break;		
		           		case "三重區":
		           			Sanchong.add(Full_Name[i]);
	           	
	           			break;
	           			case "林口區":
	           				Linkou.add(Full_Name[i]);
	           			break;		
	           			case "中和區":
	           				Chungho.add(Full_Name[i]);
	           		
	           			break;
	           			case "八里區":
	           				Bali.add(Full_Name[i]);
	           			break;		
	           			case "永和區":
	           				Yungho.add(Full_Name[i]);
	           			break;	
	           			case "深坑區":
	           				Shenkeng.add(Full_Name[i]);
	           			break;
	           			case "新莊區":
	           				Hsinchuang.add(Full_Name[i]);
	           			break;		
	           			case "石碇區":
	           			 Shihting.add(Full_Name[i]);
	           			break;
	           			case "新店區":
	           				Xindian.add(Full_Name[i]);
	           			break;	
	           		
	           			case "坪林區":
	           				Pinglin .add(Full_Name[i]);
	           		
	           			break;
	           			case "土城區":
	           				Tucheng .add(Full_Name[i]);
	           			break;		
	           			case "三芝區":
	           				Sanchih.add(Full_Name[i]);
	           			break;	
	           			case "蘆洲區":
	           				Luchou.add(Full_Name[i]);
	           			break;
	           			case "石門區":
	           				Shihmen.add(Full_Name[i]);
	           			break;		
	           			case "汐止區":
	           				Hsichih.add(Full_Name[i]);
	           			break;
	           			case "金山區":
	           				Jinshan.add(Full_Name[i]);
	           			break;	
	           			
	           			case "樹林區":
	           			 Shulin.add(Full_Name[i]);
	           		
	           			break;
	           			case "萬里區":
	           				WanLi.add(Full_Name[i]);
	           			break;		
	           			case "鶯歌區":
	           				Yingko.add(Full_Name[i]);
	           			break;	
	           			case "平溪區":
	           				Pinghsi.add(Full_Name[i]);
	           			break;
	           			case "三峽區":
	           			 Changjiang.add(Full_Name[i]);
	           			break;		
	           			case "雙溪區":
	           				Shuanghsi.add(Full_Name[i]);
	           			break;
	           			case "淡水區":
	           				Tanshui.add(Full_Name[i]);
	           			break;	
		           	
	           			case "貢寮區":
		           			Kungliao.add(Full_Name[i]);
		           		break;	
	           			case "瑞芳區":
	           				Juifang.add(Full_Name[i]);
		           		break;	
	           			case "烏來區":
	           				Wulai .add(Full_Name[i]);
		           		break;	
	           			case "五股區":
	           				Wugu.add(Full_Name[i]);
		           		break;	
		     
		           }
		           
		           if(s2.equals("板橋區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Banqiao);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("泰山區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Taishan);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("三重區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Sanchih);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("林口區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Linkou);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("中和區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Chungho);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("八里區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Bali);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("永和區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Yungho);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("深坑區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Shenkeng);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("新莊區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Hsinchuang);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		        
		           if(s2.equals("石碇區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Shihting);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("新店區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Xindian);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("坪林區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Pinglin);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("土城區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Tucheng);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("三芝區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Sanchih);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("石門區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Shihmen);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("汐止區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,	Hsichih);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("金山區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,	Jinshan);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("樹林區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Shulin);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("萬里區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,WanLi);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("鶯歌區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Yingko);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("平溪區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Pinghsi);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("三峽區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, Changjiang);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("雙溪區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, 	Shuanghsi);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("淡水區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Tanshui);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("貢寮區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Kungliao);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		    
		           
		           if(s2.equals("瑞芳區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Juifang);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("烏來區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Wulai);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("五股區"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Wugu);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
				
				
				}
			
				
				//tv_OpenData.setText(""+Wugu);
			
			 }
			 
			 catch (JSONException e)
			 {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
	
	          
			  
		}
		
	

		private String GET(String url) 
		{
			InputStream inputStream=null;
			String result="";
			try 
			{
				HttpClient httpclient=new DefaultHttpClient();
				HttpResponse httpResponse=httpclient.execute(new HttpGet(url));
				inputStream=httpResponse.getEntity().getContent();
				if(inputStream!=null)
				{
					result=covertInputStreamToString(inputStream);
				}
				else
				{
					result="Did not work";
				}
			} 
			catch (ClientProtocolException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
			return result;
		}

		private String covertInputStreamToString(InputStream inputStream) throws IOException 
		{
			BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
			String line="";
			String result="";
			// TODO Auto-generated method stub
			while((line=bufferedReader.readLine())!=null)
			{
				result+=line;
			}
			return result;
		}
		
	}
	private  class TaipeiHttpAsynoTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... urls)
		{
			// TODO Auto-generated method stub
			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) 
		{
		
			//int count=0;
			 try 
			 {
				JSONArray jsonArray = new JSONArray(result);
			
			 }
			 
			 catch (JSONException e)
			 {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			          
			  
		}

	

		private String GET(String url) 
		{
			InputStream inputStream=null;
			String result="";
			try 
			{
				HttpClient httpclient=new DefaultHttpClient();
				HttpResponse httpResponse=httpclient.execute(new HttpGet(url));
				inputStream=httpResponse.getEntity().getContent();
				if(inputStream!=null)
				{
					result=covertInputStreamToString(inputStream);
				}
				else
				{
					result="Did not work";
				}
			} 
			catch (ClientProtocolException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
			return result;
		}

		private String covertInputStreamToString(InputStream inputStream) throws IOException 
		{
			BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
			String line="";
			String result="";
			// TODO Auto-generated method stub
			while((line=bufferedReader.readLine())!=null)
			{
				result+=line;
			}
			return result;
		}
		
	}
	@Override
	public void onLocationChanged(Location location) 
	{
		// TODO Auto-generated method stub
		getLocation(location);
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderDisabled(String provider)
	{
		// TODO Auto-generated method stub
		
	}



}
