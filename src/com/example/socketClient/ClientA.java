package com.example.socketClient;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class ClientA
{
	private String remoteIp = Para.clientBIP;
	private String serverIp = Para.clientBIP;
	private int holePort = 8080;
	
	private int timeout = 60000;
	
	private Socket s;
	
	public static void main(String[] args)
	{

	}

}
