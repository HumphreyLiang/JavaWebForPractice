package com.humphrey.uploadtodatabase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.DataSource;

@WebServlet("/UploadToDataBase")
@MultipartConfig(fileSizeThreshold =500* 1024 * 1024, maxFileSize = 500 * 1024 * 1024, maxRequestSize = 5 * 500 * 1024 * 1024)
public class UploadToDataBase extends HttpServlet{
	
	String insertIMG = "INSERT INTO IMG(IMGNO, IMGNAME, IMAGE)"+"VALUES( IMG_SEQ.NEXTVAL, ? ,?)";//IMG_SEQ.NEXTVAL
	Connection con;
	PreparedStatement pstmt ;
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException{
		
		
		req.setCharacterEncoding("Big5");
		res.setContentType("text/html; charset=Big5");
		PrintWriter out = res.getWriter();
	try{	
		Collection<Part> parts = req.getParts(); 
		for(Part part: parts){
				if( part.getSize() != 0 ){
					pstmt  =con.prepareStatement(insertIMG);
					String fileName =getFileNameFromPart(part);
					InputStream in = part.getInputStream();
					pstmt.setString(1 , fileName);
					pstmt.setBinaryStream(2, in, in.available());
					pstmt.executeUpdate();
					out.println(fileName+" has Finished Upload!<br>");
				} 
			}
			pstmt.close();
		}
			catch (SQLException e) {
				e.printStackTrace();
			}
		finally{
			try{
				con.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
			out.println("Connection is close!");
		} 
		
		
	}
	public void init()	throws ServletException{
		try{
			javax.naming.Context ctx = new javax.naming.InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/WEBSERVERDB");
			con = ds.getConnection();
		}catch(Exception e){
			throw new UnavailableException("Couldn't get db connection");
		}
	}
	
	public void destory(){
		try{
			if(con !=null)
				con.close();
		}catch(SQLException e){
			System.out.println("Connection is not entirely close!");
		}
	}
	
	public String getFileNameFromPart(Part part) {
		String header = part.getHeader("content-disposition");
		String filename = header.substring(header.lastIndexOf("=") + 2, header.length() - 5);
		if (filename.length() == 0) {
			return null;
		}
		return filename;
	}
}
