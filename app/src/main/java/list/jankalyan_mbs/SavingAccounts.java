package list.jankalyan_mbs;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.RadioButton;
import android.widget.TextView;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.Accountbean;
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;
import mbLib.MyThread;

//import android.annotation.SuppressLint;

//@SuppressLint("NewApi")
public class SavingAccounts extends Activity implements OnClickListener{
	private ListView listView1;
	SavingAccounts act;
	String balnaceamnt = "",accountNo="";
	//HomeFragment homeFrag;
	Context context;
	private static final String MY_SESSION = "my_session";
	//Editor e;
	String stringValue = "";
	String all_acnts = "", str2 = "", str = "";
	String acc_type = "SAVING_CUR";
	int chekacttype=0;
	TextView txt_heading;
	ImageView img_heading;
	ImageButton btn_home,btn_back;
	Button btn_show_details;
	String acnt_inf = "";
	String accNumber = null;
	String[] prgmNameList, prgmNameListTemp;
	private ArrayList<Accountbean> Accountbean_arr;
	protected String accStr;
	DatabaseManagement dbms;
	private MyThread t1;
	 
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	public SavingAccounts(){}
	public SavingAccounts(SavingAccounts a) {
		act = a;
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.savingaccounts);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		dbms = new DatabaseManagement("list.jankalyan_mbs", "jankalyanmbs");
		listView1 = (ListView) findViewById(R.id.listView1);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		img_heading=(ImageView)findViewById(R.id.img_heading);
		
		btn_home=(ImageButton)findViewById(R.id.btn_home);
		btn_back=(ImageButton)findViewById(R.id.btn_back);
		
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);
		
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		
		btn_show_details = (Button) findViewById(R.id.btnShowDetails);
		btn_show_details.setOnClickListener(this);

		Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		stringValue=c1.getString(0);
	        }
        }
		all_acnts = stringValue;
        img_heading.setBackgroundResource(R.mipmap.term_deposit2);
        txt_heading.setText(getString(R.string.lbl_saving_and_current));

		addAccounts(all_acnts);
		
		t1 = new MyThread( Integer.parseInt(getString(R.string.Time_out)),this,var1,var3);
		t1.start();
	}

	public void addAccounts(String str)
	{
		try
		{
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");
			
			int noOfAccounts = allstr.length;

			ArrayList<Accountbean> Accountbean_arr = new ArrayList<Accountbean>();
			final ArrayList<String> Account_arrTemp = new ArrayList<String>();
			Accounts acArray[] = new Accounts[noOfAccounts];
			for (int i = 0; i < noOfAccounts; i++) 
			{
				str2 = allstr[i];
				acArray[i] = new Accounts(str2);
				str2 = str2.replaceAll("#", "-");
               	String acctype=str2.split("-")[2];
           		if ((acctype.equalsIgnoreCase("SB") || acctype.equalsIgnoreCase("CA")))
				{
					Accountbean Accountbeanobj = new Accountbean();
					Accountbean_arr.add(Accountbeanobj);
					Account_arrTemp.add(str2);
					str2 = MBSUtils.get16digitsAccNo(str2);
					Accountbeanobj.setAccountinfo(str2+" ("+MBSUtils.getAccTypeDesc(acctype)+")");
					Accountbeanobj.setAccountNumber(str2);
				}
			}

			Customlist_radioadt adapter = new Customlist_radioadt(this,
					Accountbean_arr);
			listView1.setAdapter(adapter);
			listView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapterView,
								View view, int i, long l) {
				btn_show_details.setEnabled(true);
				Accountbean dataModel = (Accountbean) adapterView.getItemAtPosition(i);
				accountNo=dataModel.getAccountNumber();
				acnt_inf = Account_arrTemp.get(i);
				for (int i1 = 0; i1 < adapterView.getCount(); i1++)
				{
					try
					{
						View v = adapterView.getChildAt(i1);
						RadioButton radio = (RadioButton) v.findViewById(R.id.radio);
						radio.setChecked(false);
					} catch (Exception e) {
						Log.e("radio button", "radio");
					}
				}

				try {
					RadioButton radio = (RadioButton) view
							.findViewById(R.id.radio);
					radio.setChecked(true);
				} catch (Exception e) {
					Log.e("radio button", "radio");
				}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btn_home)
		{
				Intent in=new Intent(this,DashboardActivity.class);
				in.putExtra("var1", var1);
				in.putExtra("var3", var3);
				startActivity(in);
				this.finish();
		}
		else if(v.getId()==R.id.btnShowDetails)
		{
			Bundle b = new Bundle();
			b.putString("accountinfo", acnt_inf);
			b.putString("accountstr", accStr);
			b.putString("accountnumber", accountNo);
			
			Intent in=new Intent(this,SavingAccountsDetails.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			in.putExtras(b);
			startActivity(in);
			this.finish();			
		}
		else if(v.getId()==R.id.btn_back)
		{
			Intent in=new Intent(this,DashboardActivity.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
			this.finish();
		}
	}

	public void showAlert(String str)
	{
			//Toast.makeText(this, str, Toast.LENGTH_LONG).show();	
			ErrorDialogClass alert = new ErrorDialogClass(this,""+str);
			alert.show();
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
		
		t1.sec =  Integer.parseInt(getString(R.string.Time_out));
		Log.e("sec11= ","sec11=="+t1.sec);
		return super.onTouchEvent(event);
	}
}
