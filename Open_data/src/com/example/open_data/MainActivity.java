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
	private boolean getService =false;//�O�_�w�}�ҩw��A��
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
	//	new NewTaipeiHttpAsynoTask().execute("http://data.ntpc.gov.tw/NTPC/od/data/api/1040400255/?$format=json");//�s�_��
	//http://data.taipei.gov.tw/opendata/apply/query/MzVERDUyOTItNjI1NC00NjcyLUE3OEItNDY3ODhDMURFM0Yy?$format=json
		//new HttpAsynoTask().execute("http://data.taipei.gov.tw/opendata/apply/query/MzVERDUyOTItNjI1NC00NjcyLUE3OEItNDY3ODhDMURFM0Yy?$format=json");//�x�_��
		//���o�t�Ωw��A��
		//http://www.ca.taipei.gov.tw/ct.asp?xItem=1071040&CtNode=40544&mp=102001 �x�_�Ҧ���
				LocationManager status=(LocationManager)(this.getSystemService(Context.LOCATION_SERVICE));
				if(status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
				{
					//�p�GGPS�κ����w��}��,�I�slocationServiceInitial()��s��m
					locationServiceInitial();
				}
				else
				{
					Toast.makeText(this, "�ж}�ҩw��A��", Toast.LENGTH_LONG).show();
					getService=true;//�T�w�}�ҩw��A��
					startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				}
				//new LocationAsynoTask().execute("http://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&sensor=false&language=zh-tw");
//				new HttpAsynoTask().execute("http://maps.googleapis.com/maps/api/geocode/json?latlng=25.047908,121.517315&sensor=false&language=zh-tw");
				new 	LocationAsynoTask().execute("http://maps.googleapis.com/maps/api/geocode/json?latlng=25.1836779,121.46377&sensor=false&language=zh-tw");
				//22.470683,114.274301
				//  25�X11'1"N   121�X24'44
				
	
	
		}
	private  class LocationAsynoTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected void onPostExecute(String result)
		{
		     try 
		    {
		    	   
		    	 	JSONArray jsonObjs =  new  JSONObject(result).getJSONArray( "results" );  
		            //���X�Ʋդ��Ĥ@��json��H(���ܨҼƲդ���ڥu�]�t�@�Ӥ���)  
		            JSONObject jsonObj = jsonObjs.getJSONObject( 0 );  
		            //�ѪR�oformatted_address��  
		            String address = jsonObj.getString( "formatted_address" ); //247�x�W�s�_��Ī�w�ϤT����42��
		            
		           // s1=address.substring(5, 8);//�s�_��
		          //  s2=address.substring(8, 11);//�s�_��
			        s3=address.indexOf("�x");//3
			        
		            s1=address.substring(s3+2, s3+5);//�s�_��
		            s2=address.substring(s3+5, s3+8);//Ī�w��
		            
		           // tv_Location.setText(s1+":"+s2+":"+s3);
		           // tv_Location.setText(address);//s1:�s�_��,s2:Ī�w��,s3����x�o�Ӧ�m
		    		Toast.makeText(getBaseContext(), address, Toast.LENGTH_LONG).show();
					
		            if(s1.equals("�s�_��")) 
					{
							Toast.makeText(getBaseContext(), "�A�{�b��m�b�s�_����", Toast.LENGTH_LONG).show();
							new NewTaipeiHttpAsynoTask().execute("http://data.ntpc.gov.tw/NTPC/od/data/api/1040400255/?$format=json");//�s�_��
						
					}
					else
					{
						
						Toast.makeText(getBaseContext(), "�A�{�b��m�b�x�_����", Toast.LENGTH_LONG).show();//http://data.taipei.gov.tw/opendata/apply/NewDataContent?oid=35DD5292-6254-4672-A78B-46788C1DE3F2
						new NewTaipeiHttpAsynoTask().execute("http://data.ntpc.gov.tw/NTPC/od/data/api/1040400255/?$format=json");//�s�_��
						
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
		lms=(LocationManager)getSystemService(LOCATION_SERVICE);//���o�t�Ωw��A��
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
			Toast.makeText(this, "�L�k�w��A��",Toast.LENGTH_LONG).show();
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
			
			//Banqiao �O��,Taishan ���s, Sanchong�T��,Linkou �L�f,Chungho ���M ,Bali �K��, Yungho �éM, Shenkeng �`�|, Hsinchuang �s�� , Shihting ����,Xindian �s�� , Pinglin  �W�L,Tucheng  �g��
			//�T��   Sanchih,Ī�w Luchou,�۪� Shihmen,����  Hsichih,���s  Jinshan,��L Shulin ,�U�� WanLi,�a�q Yingko,���� Pinghsi ,�T�l    Changjiang ,���� ] Shuanghsi ,�H��   Tanshui, �^�d  Kungliao , ���  Juifang , �Q��   Wulai 
			//���� Wugu
			
		
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
		            Part_add[i]=Add;//������Add(�Q���ΰϪ�����)
		            Full_add[i]=Add;//���㪺Add
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
		            }else//����Add�S���ԲӸ�� �u�����Ӧr
		            {
		            	Part_add[i]="";
		           	 //Log.i("less6", "name: " + All_add[i] );
		           	 //Toast.makeText(getApplicationContext(), All_add[i]+":", Toast.LENGTH_SHORT).show();
					    
		            }
		            
		        	
					//Banqiao �O��,Taishan ���s, Sanchong�T��,Linkou �L�f,Chungho ���M ,Bali �K��, Yungho �éM, Shenkeng �`�|, Hsinchuang �s�� , Shihting ����,Xindian �s�� , Pinglin  �W�L,Tucheng  �g��
					//�T��   Sanchih,Ī�w Luchou,�۪� Shihmen,����  Hsichih,���s  Jinshan,��L Shulin ,�U�� WanLi,�a�q Yingko,���� Pinghsi ,�T�l    Changjiang ,����  Shuanghsi ,�H��   Tanshui, �^�d  Kungliao , ���  Juifang , �Q��   Wulai 
					//���� Wugu
					
		           switch(Part_add[i])
		           {
		           		case "�O����":
		           			Banqiao.add(Full_Name[i]);
		           		break;	
		        
		           		case "���s��":
		           			Taishan.add(Full_Name[i]);
		           		break;		
		           		case "�T����":
		           			Sanchong.add(Full_Name[i]);
	           	
	           			break;
	           			case "�L�f��":
	           				Linkou.add(Full_Name[i]);
	           			break;		
	           			case "���M��":
	           				Chungho.add(Full_Name[i]);
	           		
	           			break;
	           			case "�K����":
	           				Bali.add(Full_Name[i]);
	           			break;		
	           			case "�éM��":
	           				Yungho.add(Full_Name[i]);
	           			break;	
	           			case "�`�|��":
	           				Shenkeng.add(Full_Name[i]);
	           			break;
	           			case "�s����":
	           				Hsinchuang.add(Full_Name[i]);
	           			break;		
	           			case "�����":
	           			 Shihting.add(Full_Name[i]);
	           			break;
	           			case "�s����":
	           				Xindian.add(Full_Name[i]);
	           			break;	
	           		
	           			case "�W�L��":
	           				Pinglin .add(Full_Name[i]);
	           		
	           			break;
	           			case "�g����":
	           				Tucheng .add(Full_Name[i]);
	           			break;		
	           			case "�T�۰�":
	           				Sanchih.add(Full_Name[i]);
	           			break;	
	           			case "Ī�w��":
	           				Luchou.add(Full_Name[i]);
	           			break;
	           			case "�۪���":
	           				Shihmen.add(Full_Name[i]);
	           			break;		
	           			case "�����":
	           				Hsichih.add(Full_Name[i]);
	           			break;
	           			case "���s��":
	           				Jinshan.add(Full_Name[i]);
	           			break;	
	           			
	           			case "��L��":
	           			 Shulin.add(Full_Name[i]);
	           		
	           			break;
	           			case "�U����":
	           				WanLi.add(Full_Name[i]);
	           			break;		
	           			case "�a�q��":
	           				Yingko.add(Full_Name[i]);
	           			break;	
	           			case "���˰�":
	           				Pinghsi.add(Full_Name[i]);
	           			break;
	           			case "�T�l��":
	           			 Changjiang.add(Full_Name[i]);
	           			break;		
	           			case "���˰�":
	           				Shuanghsi.add(Full_Name[i]);
	           			break;
	           			case "�H����":
	           				Tanshui.add(Full_Name[i]);
	           			break;	
		           	
	           			case "�^�d��":
		           			Kungliao.add(Full_Name[i]);
		           		break;	
	           			case "��ڰ�":
	           				Juifang.add(Full_Name[i]);
		           		break;	
	           			case "�Q�Ӱ�":
	           				Wulai .add(Full_Name[i]);
		           		break;	
	           			case "���Ѱ�":
	           				Wugu.add(Full_Name[i]);
		           		break;	
		     
		           }
		           
		           if(s2.equals("�O����"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Banqiao);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("���s��"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Taishan);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("�T����"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Sanchih);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("�L�f��"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Linkou);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("���M��"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Chungho);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("�K����"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Bali);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("�éM��"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Yungho);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("�`�|��"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Shenkeng);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("�s����"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Hsinchuang);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		        
		           if(s2.equals("�����"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Shihting);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("�s����"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Xindian);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("�W�L��"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Pinglin);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("�g����"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Tucheng);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("�T�۰�"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Sanchih);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("�۪���"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Shihmen);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("�����"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,	Hsichih);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("���s��"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,	Jinshan);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("��L��"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Shulin);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("�U����"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,WanLi);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("�a�q��"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Yingko);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("���˰�"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Pinghsi);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("�T�l��"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, Changjiang);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("���˰�"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, 	Shuanghsi);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
		           if(s2.equals("�H����"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Tanshui);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("�^�d��"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Kungliao);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		    
		           
		           if(s2.equals("��ڰ�"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Juifang);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("�Q�Ӱ�"))
		           {
		        	   laMuilt=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,Wulai);
		       		listView.setAdapter(laMuilt);
		       		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		           }
				
		           if(s2.equals("���Ѱ�"))
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
