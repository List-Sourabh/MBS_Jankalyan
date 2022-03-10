package list.jankalyan_mbs;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.crypto.spec.SecretKeySpec;

import androidx.core.content.FileProvider;
import mbLib.CustomDialogClass;
import mbLib.MBSUtils;
import mbLib.MiniStatementBean;
import mbLib.MyThread;
//import android.annotation.SuppressLint;

//@SuppressLint("NewApi")
public class MiniStmtReport extends Activity implements OnClickListener {
	Activity act;
	MiniStmtReport miniStmtRpt;
	TextView accNo, branch, sch_acno, name, bal,avil_bal;
	ImageButton btn_home,back;
	String actype_val, branch_val, sch_acno_val, name_val, bal_val;
	String str = "", spi_str = "";
	String balance,retMess,avilablebal;
	ListView listView1 ;
	List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	public MiniStmtReport(){}
	String stringValue;
	String amnt="";
	TextView txt_heading;
	ImageView img_heading;
	ArrayList<MiniStatementBean> MiniStmntBeanArray;
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_mini_stmt_report);		
		//System.out.println("MiniStmtReport onCreateView()");
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		act=this;
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
        //System.out.println("initAll()");
		Intent getdata=getIntent();
		Bundle b1=getdata.getExtras();
		if(b1!=null)
		{
			amnt=b1.getString("balnaceamnt");
			str=b1.getString("str");
			spi_str=b1.getString("all_str");
			retMess=b1.getString("retval");
			
			avilablebal=b1.getString("avil_bal");
			
			
		}
		accNo = (TextView) findViewById(R.id.txt_actype);
		bal=(TextView) findViewById(R.id.cur_bal);
	    avil_bal=(TextView)findViewById(R.id.avil_bal);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		img_heading=(ImageView)findViewById(R.id.img_heading);
		try
		{
		bal.setText(amnt.trim());
         	avil_bal.setText(avilablebal.trim());
	
		}
		catch(Exception e)
		{
			Log.e("Mini Statement Report", ""+e);
		}
		
		//branch = (TextView) rootView.findViewById(R.id.txt_brno);
	    //sch_acno = (TextView) rootView.findViewById(R.id.txt_schm_acno);
		/*name = (TextView) rootView.findViewById(R.id.txt_cust_name);/*
		bal = (TextView) findViewById(R.id.txt_bal);*/
		back = (ImageButton)findViewById(R.id.btn_back);
		btn_home=(ImageButton)findViewById(R.id.btn_home);
		
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		back.setImageResource(R.mipmap.backover);
		
		btn_home.setOnClickListener(this);
		back.setOnClickListener(this);
		
		//back.setTypeface(tf_calibri);			
		listView1 = (ListView)findViewById(R.id.listView1);
		
		setValues();
     
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
    }
	
	public void setValues() 
	{
		txt_heading.setText(getString(R.string.lbl_mini_statement));
		img_heading.setBackgroundResource(R.mipmap.checkbkstatus2);
		String sel_str = spi_str;
		sel_str=sel_str.replaceAll("-", "#");
		Accounts acObj=new Accounts(sel_str);
		if(acObj.getAccType().equalsIgnoreCase("SB"))
		{
			actype_val = "Savings";
		}
		else if(acObj.getAccType().equalsIgnoreCase("LO"))
		{
			actype_val = "Loan";
		}
		else if(acObj.getAccType().equalsIgnoreCase("RP"))
		{
			actype_val = "Re-Investment Plan";
		}
		else if(acObj.getAccType().equalsIgnoreCase("FD"))
		{
			actype_val = "Fixed Deposite";
		}
		else if(acObj.getAccType().equalsIgnoreCase("CA"))
		{
			actype_val = "Current Account";
		}
		
		branch_val = acObj.getBrCd();// "Main Branch"; 
		sch_acno_val= acObj.getSchCd()+ "-" + acObj.getAccNo();
		name_val = acObj.getHolderName();
		
		String trn_str=retMess;
		//trn_str="SUCCESS~12/12/2012#ATM WITHDRAWL VBAG#1000#DR~15/12/2012#CHEQUE PAYMENT#2000#CR~18/12/2012#NEFT TRANSFER#500#DR~20/12/2012#CASH DEPOSIT#5000#CR~28/12/2012#INTEREST RECVD#700#DR";
		//System.out.println("trn_str:"+trn_str);
		String str1[] = trn_str.split("~");
		//System.out.println("str1[0]:"+str1[0]);
		if(str1[0].indexOf("SUCCESS")>-1)
		{
			//System.out.println("str1[1]:"+str1[1]);
			//String string1[]=str1[1].split(",-,");
			//System.out.println("strring1.length:"+str1.length);
			//List<String> content = new ArrayList<String>();
			
			MiniStmntBeanArray=new ArrayList<MiniStatementBean>(); 
			for (int j = 1; j < str1.length; j++) 
			{
				//Log.i("MINI STMT","IN 2nd FOR........");
				//System.out.println("j..............................:"+j);
			    //System.out.println("string1"+str1[j]);
				MiniStatementBean beanObj=new MiniStatementBean(); 
				String string2[] = str1[j].split("#");
				HashMap<String, String> map = new HashMap<String, String>();
				String[] from = new String[] {"rowid", "col_0", "col_1", "col_2","col_3","col_5"};
				int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4,R.id.item5,R.id.item7 };
				
			    map.put("col_0", string2[0].trim());			       
			    map.put("col_1", properCase(string2[1].trim()));
			    map.put("col_2",""+MBSUtils.amountFormat(string2[2].trim(),false,act));
			    map.put("col_3",  string2[3].trim() );	
			    
			    beanObj.setDate(string2[0].trim());
			    beanObj.setDescr(properCase(string2[1].trim()));
			    beanObj.setAmount(""+MBSUtils.amountFormat(string2[2].trim(),false,act));
			    beanObj.setDrCr(string2[3].trim());
			    MiniStmntBeanArray.add(beanObj);
			    fillMaps.add(map);
				/*SimpleAdapter adapter = new SimpleAdapter(act, fillMaps, R.layout.mini_stmt_list, from, to);
		        listView1.setAdapter(adapter);*/
			}
			if(MiniStmntBeanArray.size()>0)
			{
				CustomAdapterMiniStatement ada=new CustomAdapterMiniStatement(act, MiniStmntBeanArray);
				listView1.setAdapter(ada);
			}
			accNo.setText(MBSUtils.get16digitsAccNo(str));			
		}
		listView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView1
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView,
											View view, int i, long l) {
						ArrayList<MiniStatementBean> MiniStmntBean = new ArrayList<MiniStatementBean>();
						MiniStmntBean=MiniStmntBeanArray;
						String strdate=MiniStmntBean.get(i).getDate();
						String strcrdr=MiniStmntBean.get(i).getDrCr();
						String stramount=MiniStmntBean.get(i).getAmount();
						String strdesc=MiniStmntBean.get(i).getDescr();
						String sharestring=strdate+"\t"+strcrdr+" \t"+stramount+"\t"+strdesc;
						showshareAlert(sharestring);
					}
				});
	}

	public void showshareAlert(final String str)
	{
		CustomDialogClass alert=new CustomDialogClass(act, str) {
			@Override
			protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.custom_dialog_box);
				Button btn = (Button) findViewById(R.id.btn_cancel);
				TextView txt_message=(TextView)findViewById(R.id.txt_dia);
				txt_message.setText(str);
				btn.setOnClickListener(this);
				btn.setText("Share");
				Button btnok = (Button) findViewById(R.id.btn_ok);
				btnok.setOnClickListener(this);
				btnok.setText("OK");
			}
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.btn_ok:
						this.dismiss();
						break;

					case R.id.btn_cancel:
//						String shareBody = null;
//						shareBody = "Beneficiary Name : ";
//						Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//						sharingIntent.setType("text/plain");
//						sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
//						sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
//						startActivity(sharingIntent);


						Calendar c = Calendar.getInstance();
						SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
						String CurrentDateTime = sdf.format(c.getTime());
						Intent share = new Intent(Intent.ACTION_SEND);
						Bitmap src = BitmapFactory.decodeResource(getResources(), R.mipmap.bg); // the original file yourimage.jpg i added in resources
						Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

						String shareBody1 = "Paid On : "+"CurrentDateTimeMMMMMMMMMMMMM";//mm
						Typeface typeface1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/calibri.ttf");
						Canvas cs = new Canvas(dest);
						Paint tPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
						tPaint.setTypeface(typeface1);
						tPaint.setTextSize(20);
						tPaint.setColor(Color.BLACK);
						cs.drawBitmap(src, 0f, 0f, null);
						float height = tPaint.measureText("yY");
						float width = tPaint.measureText(shareBody1);
						float x_coord = (src.getWidth() - width)/2;//jObj.getString("BENFNAME")
						height=100f;

						//String sharestring=strdate+"\t"+strcrdr+" \t"+stramount+"\t\n"+strdesc;

						String []arr = str.split("\t");









						cs.drawText("Date : "+arr[0], 20, height+20f, tPaint);//270 100
						cs.drawText("Account No : "+accNo.getText().toString(), 25, height+60f, tPaint);//220
						cs.drawText("Amount : "+arr[2], 20, height+100f, tPaint);//320
						cs.drawText("Remark : ", 20, height+130f, tPaint);//370
						//cs.drawText(chenextlne(str), x_coord, height+370f, tPaint);//370
						MBSUtils.drawMultilineText(cs,str,20,height+150f,tPaint);
						try {
							dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File("/sdcard/SharedImage.jpg")));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						// dest is Bitmap, if you want to preview the final image, you can display it on screen also before saving
						share.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						Uri apkURI = FileProvider.getUriForFile(act,act.getPackageName(), new File("/sdcard/SharedImage.jpg"));
						share.putExtra(Intent.EXTRA_STREAM,	Uri.parse("file:///sdcard/SharedImage.jpg"));
						share.putExtra(Intent.EXTRA_STREAM,apkURI);
						share.setType("image/*");
						startActivityForResult(Intent.createChooser(share, "Share Image"),0);
						
						break;
					default:
						break;
				}
				dismiss();


			}
		};
		alert.show();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			Intent in1=new Intent(act,MiniStmtActivity.class);
			in1.putExtra("var1", var1);
			   in1.putExtra("var3", var3);
			startActivity(in1);
			act.finish();
			break;
		case R.id.btn_home:
			Intent in=new Intent(act,DashboardActivity.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
			act.finish();
			break;
			
		}
	}
	
	public String properCase(String input)
	{		
		StringBuffer sb = new StringBuffer();

		StringTokenizer tokens = new StringTokenizer(input, " ");
		while (tokens.hasMoreTokens())
		{
		    String part=tokens.nextToken();
		    char[] chars = part.toLowerCase().toCharArray();
			chars[0] = Character.toUpperCase(chars[0]);

			sb.append(new String(chars)).append(" ");
		}		
		return sb.toString().trim();
	}
	
	@Override
	public void onBackPressed() {
	 // Simply Do noting!
	}	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		t1.sec=-1;
		System.gc();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		t1.sec = timeOutInSecs;
		Log.e("sec11= ","sec11=="+t1.sec);
		return super.onTouchEvent(event);
	}
}
