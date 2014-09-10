package jsoup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Data {
	private String id;
	private String force;
	private String date;
	private String portal;
	private String portalName;
	private String portalPlng;
	private String portalPlat;
	private String info;
	public void setId(String id){
		this.id = id;
	}
	public void setForce(String force){
		this.force = force;
	}
	public void setDate(String date){
		this.date = date;
	}
	public void setPortal(String portal){
		this.portal = portal;
	}
	public void setPortalName(String portalName){
		this.portalName = portalName;
	}
	public void setPortalPlng(String portalPlng){
		this.portalPlng = portalPlng;
	}
	public void setPortalPlat(String portalPlat){
		this.portalPlat = portalPlat;
	}
	public void setInfo(String info){
		this.info = info;
	}
	public String getId(){
		return id;
	}
	public String getForce(){
		return force;
	}
	public String getDate(){
		return date;
	}
	public String getPortal(){
		return portal;
	}
	public String getInfo(){
		return info;
	}
	public String getPortalName(){
		return portalName;
	}
	public String getPortalPlng(){
		return portalPlng;
	}
	public String getPortalPlat(){
		return portalPlat;
	}
	public void dataInit(){
		String[] p = portalAddress(portal);
		this.portalName = commaRemover(htmlRemover(portalName(portal)));
		this.portalPlng = p[0];
		this.portalPlat = p[1];
	}

	private static String[] portalAddress(String target){
//<span data-plng="139.816755" data-plat="35.732525" class="pl_portal_name">首塚地蔵尊</span>
		//文脈を見てStringの数を変更しなければいけない destroyed linked(現在ははじめの2つのデータしか読まないためlinkedの場合link先の情報は消している。
		String[] array = new String[2];
		 Pattern p = Pattern.compile("[0-9]+.[0-9]+");
	     Matcher m = p.matcher(target);
	     int i=0;
	     	while(m.find()){
	        	String s = m.group();
	        	array[i] = s;
	        	if(i==1)
	        		break;
	        	else
	        		i++;
	        }
		return array;
	}
	private static String portalName(String target){
		//<span data-plng="139.816755" data-plat="35.732525" class="pl_portal_name">首塚地蔵尊</span>
				//文脈を見てStringの数を変更しなければいけない destroyed linked(現在ははじめの2つのデータしか読まないためlinkedの場合link先の情報は消している。
				String pn = new String();
				 Pattern p = Pattern.compile("[<span]?.+[span>]?");
			     Matcher m = p.matcher(target);
			     	if(m.find()){
			        	String s = m.group();
			        	pn = s;
			        }
				return pn;
			}
/*
	public static void main(String[] args){
		String target = "<span data-plng=\"139.816755\" data-plat=\"35.732525\" class=\"pl_portal_name\">首塚地蔵尊</span>";
		String[] t = portalAddress(target);
		for(int i=0;i<t.length;i++){
			System.out.println(t[i]);
		}
		System.out.println(htmlRemover(target));
		Data d = new Data();
		d.setPortal(target);
		d.dataInitialize();
		System.out.println("plat" + d.getPortalPlat() + "plng" + d.getPortalPlng() + "name" + d.getPortalName() + d.getForce());

	}
	*/
    //HTMLのタグ除去をする。
    private static String htmlRemover(String str){
    	return str.replaceAll("<.+?>", "");
    }
    //HTMLのタグ除去をする。
    private static String commaRemover(String str){
    	return str.replaceAll(",", ".");
    }
    public String outputCSVInit(){
    	StringBuilder sbt = new StringBuilder();
    	//日付、ID,FORCE、PORTALNAME，PORTALNAME-plng,portalplat
    	sbt.append("date").append(",").append("id").append(",").append("force").append(",").append("portal-name").append(",").append("portal-plng").append(",").append("portal-plat").append("\n");
		return sbt.toString();
    }
    @Override
	public String toString(){
		StringBuilder sbt = new StringBuilder();
		sbt.append(date).append(",").append(id).append(",").append(force).append(",").append(portalName).append(",").append(portalPlng).append(",").append(portalPlat).append("\n");
		return sbt.toString();
	}

}
