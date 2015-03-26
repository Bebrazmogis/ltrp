


#define ERROR_LOG_FILE					"server_log.err"


ErrorLog(const text[], {Float, _}:...)
{
	new buffer[256], tmp[128], type, argumentCount = 1, File:handle, bufferlen, hour, minute, second;

	handle = f_open(ERROR_LOG_FILE, "a");
	if(!handle)
		return 0;

	static bool:isFirstCall = true;
	if(isFirstCall)
	{
		isFirstCall = false;
		new year, month, day;
		getdate(year, month, day);
		f_write(handle, "\r\n--------------------------------------");	
		format(buffer, sizeof(buffer), "Error log started on %d.%d.%d\r\n",year,month,day);
		f_write(handle, buffer);
		f_write(handle, "--------------------------------------\r\n\r\n");
	}

	gettime(hour, minute, second);
	format(buffer, sizeof(buffer), "[%2d.%2d.%2d]", hour, minute, second);
	f_write(handle, buffer);

	for(new i = 0; i < strlen(text)-1; i++)
	{
		if(text[ i ] == '%' && text[ i + 1] != '%') 
		{
			type = text[ i + 1];
			switch(type)
			{
				case 'd', 'i':
				{
					strdel(buffer, bufferlen, bufferlen+2);
					valstr(tmp, getarg(argumentCount));
					strins(buffer, tmp, bufferlen, strlen(tmp));
					bufferlen += strlen(tmp);
				}
				case 'f':
				{
					strdel(buffer, bufferlen, bufferlen+2);
					format(tmp, sizeof(tmp),"%f", getarg(argumentCount));
					strins(buffer, tmp, bufferlen, strlen(tmp));
					bufferlen += strlen(tmp);
				}
				case 's':
				{
					strdel(buffer, bufferlen, bufferlen+2);
					new len, index = 0;
				    for( ;; )
				    {
						new chari = getarg(argumentCount, index++);
						tmp[ len++ ] = chari;
						if(chari == 0) 
							break;
				    }
					strins(buffer, tmp, bufferlen, strlen(tmp));
					bufferlen += strlen(tmp);
				}
				default: 
					continue;
			}
			argumentCount++;
			i++;
		}
		else buffer[ bufferlen++ ] = text[ i ];
	}
	f_write(handle, buffer);
	f_write(handle, "\r\n");
	f_close(handle);
	return 1;
}