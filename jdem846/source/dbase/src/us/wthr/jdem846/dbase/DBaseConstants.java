/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.dbase;

public class DBaseConstants {
	public static final int DBASE_PROP_REQUIRED           = 1;
	public static final int DBASE_PROP_MIN                = 2;
	public static final int DBASE_PROP_MAX                = 3;
	public static final int DBASE_PROP_DEFAULT            = 4;
	public static final int DBASE_PROP_DB_CONSTRAINT      = 6;

	public static final int DBASE_TYPE_CHAR               = 0x43;
	public static final int DBASE_TYPE_NUMERIC            = 0x4E;
	public static final int DBASE_TYPE_MEMO               = 0x4D;
	public static final int DBASE_TYPE_LOGICAL            = 0x4C;
	public static final int DBASE_TYPE_DATE               = 0x44;
	public static final int DBASE_TYPE_FLOAT              = 0x46;
	public static final int DBASE_TYPE_GENERAL            = 0x77;
	public static final int DBASE_TYPE_PICTURE            = 0x50;


	public static final byte DBASE_FIELD_DESCRIP_TERM     = 0x0D;

	public static final int DBASE_VER_DBASEIV_NO_MEMO     = 0x03; 
	public static final int DBASE_VER_DBASEIV_W_MEMO      = 0x8B; 
	public static final int DBASE_VER_DBASEIV_W_SQL       = 0x8E;

	public static final int DBASE_PROD_INDEX_EXISTS       = 0x01;
	public static final int DBASE_NO_PROD_INDEX           = 0x00;

	public static final int DBASE_RECORD_DELETED          = 0x2A;
	public static final int DBASE_RECORD_ACTIVE           = 0x20;
}
