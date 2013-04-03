package us.wthr.jdem846.nasa.pds;

public enum PdsDataTypeEnum
{
	CHARACTER,
	ALPHABET,
	ALPHANUMERIC,
	NUMERIC,
	INTEGER,
	REAL,
	NONDECIMAL,
	
/*
	YYYY-MM-DDThh:mm:ss[.fff] -or- YYYY-DDDThh:mm:ss[.fff]
	
YYYY Represents the year (0001 to 9999)
- Is a required delimiter between date fields
MM Represents the month (01 to 12)
DD Represents the day of month (01 to 28, 29, 30 or 31)
DDD Represents the day of year (001 to 365 or 366)
T Is a required delimiter between date and time
hh Represents the UTC hour (00 to 23)
: Is a required delimiter between time fields
mm Represents the UTC minute (00 to 59)
ss Represents UTC whole seconds (00 to 60)
fff Represents fractional seconds, from one to three decimal places
*/
	TIME,
	
	
/*
 * YYYY-MM-DD -or- YYYY-DDD
 */
	DATE
}
