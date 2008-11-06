package gsn.vsensor;

import gsn.beans.DataTypes;
import gsn.beans.StreamElement;
import gsn.beans.VSensorConfig;

import org.apache.log4j.Logger;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

/** 
 * This virtual sensor is used for accessing Sensorscope data with
 * MigMessageWrapper.
 */

public class SensorscopeVS extends AbstractVirtualSensor {

	private static final transient Logger logger = Logger.getLogger( SensorscopeVS.class );

	private int SAMPLING_TIME = 30000;
	private static final int NO_VALUE      = Short.MIN_VALUE;


	private static final String NTW_SENDER_ID = "NTWSENDERID";
	private static final String NTW_DISTANCE_TO_BTS = "NTWDISTTOBS";
	private static final String TSP_HOP_COUNT = "TSPHOPCOUNT";
	private static final String TSP_PACKET_SN = "TSPPACKETSN";
	private static final String REPORTER_ID = "REPORTERID";
	private static final String TIMESTAMP = "TIMESTAMP";
	private static final String RAIN_METER = "RAINMETER";
	private static final String WIND_SPEED = "WINDSPEED";
	private static final String WATERMARK = "WATERMARK";
	private static final String SOLAR_RADIATION = "SOLARRADIATION";
	private static final String AIR_TEMPERATURE = "AIRTEMPERATURE";
	private static final String AIR_HUMIDITY = "AIRHUMIDITY";
	private static final String SKIN_TEMPERATURE = "SKINTEMPERATURE";
	private static final String SOIL_MOISTURE = "SOILMOISTURE";
	private static final String WIND_DIRECTION = "WINDDIRECTION";
	private static final String WIND_DIRECTION2 = "WINDDIRECTION2";
	private static final String SOIL_CONDUCTIVITY_1 = "SOILCONDUCTIVITY1";
	private static final String SOIL_CONDUCTIVITY_2 = "SOILCONDUCTIVITY2";
	private static final String SOIL_CONDUCTIVITY_3 = "SOILCONDUCTIVITY3";
	private static final String SOIL_MOISTURE_1	= "SOILMOISTURE1";
	private static final String SOIL_MOISTURE_2	= "SOILMOISTURE2";
	private static final String SOIL_MOISTURE_3	= "SOILMOISTURE3";
	private static final String SOIL_TEMPERATURE_1 = "SOILTEMPERATURE1";
	private static final String SOIL_TEMPERATURE_2 = "SOILTEMPERATURE2";
	private static final String SOIL_TEMPERATURE_3 = "SOILTEMPERATURE3";
	private static final String FOO = "FOO";

	public boolean initialize ( ) {
		VSensorConfig vsensor = getVirtualSensorConfiguration();
		TreeMap < String , String > params = vsensor.getMainClassInitialParams( );
		String sampling_time = params.get("sampling");
		if(sampling_time != null) {
			try {
				Long st = Long.parseLong(sampling_time);
				SAMPLING_TIME = st.intValue();
			}
			catch (NumberFormatException e) {
				logger.debug(e.getMessage());
			}
		}
		logger.debug("Sampling time : >" + SAMPLING_TIME + "<");
		return true;
	}

	/**
	 * This method is called whenever there is new data coming to
	 * virtual sensor.
	 * In this case the data structure should apply the one used
	 * in sensorscope.
	 * If some data can't be read, then a value of smallest short
	 * -32768 is returned for ints, shorts and doubles.
	 */

	public void dataAvailable ( String inputStreamName , StreamElement data ) {
		Serializable[] dataFields = data.getData();
		short ntwSenderId = NO_VALUE;
		short ntwDistToBts = NO_VALUE;
		short tspHopCount = NO_VALUE;
		short tspPacketSn = NO_VALUE;
		short reporterId = NO_VALUE;
		long timestamp = NO_VALUE;
		double rainMeter = NO_VALUE;
		double windSpeed = NO_VALUE;
		double watermark = NO_VALUE;
		double solarRadiation = NO_VALUE;
		double airTemperature = NO_VALUE;
		double airHumidity = NO_VALUE;
		double skinTemperature = NO_VALUE;
		double soilMoisture = NO_VALUE;
		double windDirection = NO_VALUE;
		double windDirection2 = NO_VALUE;
		double soilConductivity1 = NO_VALUE;
		double soilConductivity2 = NO_VALUE;
		double soilConductivity3 = NO_VALUE;
		short soilMoisture1 = NO_VALUE;
		short soilMoisture2 = NO_VALUE;
		short soilMoisture3 = NO_VALUE;
		double soilTemperature1 = NO_VALUE;
		double soilTemperature2 = NO_VALUE;
		double soilTemperature3 = NO_VALUE;
		short foo = 0;

		// Air temperature is needed afterwards by watermark and humidity,
		// so it has to be calculated first
		int i = 0;
		for(String fieldName : data.getFieldNames()) {
			fieldName = fieldName.toUpperCase();
			if(fieldName.equals(AIR_TEMPERATURE)) {
				airTemperature = getTemperature((Integer) dataFields[i]);
			}
			i++;
		}

		// Output structure is adjusted dynamically depending on what input fields
		// are got.
		// fieldNames is for the field names of input stream element, they
		//    will be the same for output as well
		// dataTypes is for datatypes of the output values. These are set
		//    statically, because the input values are all int or short and
		//    outputs are short or double
		// datas has the actual data, which is calculated here
		ArrayList<String> fieldNames = new ArrayList<String>();
		ArrayList<Byte> dataTypes = new ArrayList<Byte>();
		ArrayList<Serializable> datas = new ArrayList<Serializable>();
		i=0;
		for(String fieldName : data.getFieldNames()) {
			fieldName = fieldName.toUpperCase();
			if (fieldName.equals(SOIL_CONDUCTIVITY_1)) {
				soilConductivity1 = getSoilConductivity((Short)dataFields[i]);
				fieldNames.add(SOIL_CONDUCTIVITY_1);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(soilConductivity1);
			}
			if (fieldName.equals(SOIL_CONDUCTIVITY_2)) {
				soilConductivity2 = getSoilConductivity((Short)dataFields[i]);
				fieldNames.add(SOIL_CONDUCTIVITY_2);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(soilConductivity2);
			}
			if (fieldName.equals(SOIL_CONDUCTIVITY_3)) {
				soilConductivity3 = getSoilConductivity((Short)dataFields[i]);
				fieldNames.add(SOIL_CONDUCTIVITY_3);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(soilConductivity3);
			}
			//
			if (fieldName.equals(SOIL_MOISTURE_1)) {
				soilMoisture1 = (Short)dataFields[i];
				fieldNames.add(SOIL_MOISTURE_1);
				dataTypes.add(DataTypes.SMALLINT);
				datas.add(soilMoisture1);
			}
			if (fieldName.equals(SOIL_MOISTURE_2)) {
				soilMoisture2 = (Short)dataFields[i];
				fieldNames.add(SOIL_MOISTURE_2);
				dataTypes.add(DataTypes.SMALLINT);
				datas.add(soilMoisture2);
			}
			if (fieldName.equals(SOIL_MOISTURE_3)) {
				soilMoisture3 = (Short)dataFields[i];
				fieldNames.add(SOIL_MOISTURE_3);
				dataTypes.add(DataTypes.SMALLINT);
				datas.add(soilMoisture3);
			}
			//
			if (fieldName.equals(SOIL_TEMPERATURE_1)) {
				soilTemperature1 = getSoilTemperature((Short)dataFields[i]);
				fieldNames.add(SOIL_TEMPERATURE_1);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(soilTemperature1);
			}
			if (fieldName.equals(SOIL_TEMPERATURE_2)) {
				soilTemperature2 = getSoilTemperature((Short)dataFields[i]);
				fieldNames.add(SOIL_TEMPERATURE_2);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(soilTemperature2);
			}
			if (fieldName.equals(SOIL_TEMPERATURE_3)) {
				soilTemperature3 = getSoilTemperature((Short)dataFields[i]);
				fieldNames.add(SOIL_TEMPERATURE_3);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(soilTemperature3);
			}
			//
			if(fieldName.equals(NTW_SENDER_ID)) {
				ntwSenderId = (Short) dataFields[i];
				fieldNames.add(NTW_SENDER_ID);
				dataTypes.add(DataTypes.SMALLINT);
				datas.add(ntwSenderId);
			} else if(fieldName.equals(NTW_DISTANCE_TO_BTS)) {
				ntwDistToBts = (Short) dataFields[i];
				fieldNames.add(NTW_DISTANCE_TO_BTS);
				dataTypes.add(DataTypes.SMALLINT);
				datas.add(ntwDistToBts);
			} else if(fieldName.equals(TSP_HOP_COUNT)) {
				tspHopCount = (Short) dataFields[i];
				fieldNames.add(TSP_HOP_COUNT);
				dataTypes.add(DataTypes.SMALLINT);
				datas.add(tspHopCount);
			} else if(fieldName.equals(TSP_PACKET_SN)) {
				tspPacketSn = (Short) dataFields[i];
				fieldNames.add(TSP_PACKET_SN);
				dataTypes.add(DataTypes.SMALLINT);
				datas.add(tspPacketSn);
			} else if(fieldName.equals(REPORTER_ID)) {
				reporterId = (Short) dataFields[i];
				fieldNames.add(REPORTER_ID);
				dataTypes.add(DataTypes.SMALLINT);
				datas.add(reporterId);
			} else if(fieldName.equals(TIMESTAMP)) {
				timestamp = (Long) dataFields[i];
				fieldNames.add(TIMESTAMP);
				dataTypes.add(DataTypes.BIGINT);
				datas.add(timestamp);
			} else if(fieldName.equals(RAIN_METER)) {
				rainMeter = getRainMeter((Short) dataFields[i]);
				fieldNames.add(RAIN_METER);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(rainMeter);
			} else if(fieldName.equals(WIND_SPEED)) {
				windSpeed = getWindSpeed((Integer) dataFields[i]);
				fieldNames.add(WIND_SPEED);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(windSpeed);
			} else if(fieldName.equals(WATERMARK)) {
				watermark = getWatermark((Integer) dataFields[i], airTemperature);
				fieldNames.add(WATERMARK);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(watermark);
			} else if(fieldName.equals(SOLAR_RADIATION)) {
				solarRadiation = getSolarRadiation((Integer) dataFields[i]);
				fieldNames.add(SOLAR_RADIATION);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(solarRadiation);
			} else if(fieldName.equals(AIR_TEMPERATURE)) {
				fieldNames.add(AIR_TEMPERATURE);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(airTemperature);
			} else if(fieldName.equals(AIR_HUMIDITY)) {
				airHumidity = getHumidity((Integer) ((Number)dataFields[i]).intValue(), airTemperature);
				fieldNames.add(AIR_HUMIDITY);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(airHumidity);
			} else if(fieldName.equals(SKIN_TEMPERATURE)) {
				skinTemperature = getSkinTemperature((Integer) ((Number)dataFields[i]).intValue());
				fieldNames.add(SKIN_TEMPERATURE);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(skinTemperature);
			} else if(fieldName.equals(SOIL_MOISTURE)) {
				soilMoisture = getSoilMoisture((Integer) ((Number)dataFields[i]).intValue());
				fieldNames.add(SOIL_MOISTURE);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(soilMoisture);
			} else if(fieldName.equals(WIND_DIRECTION)) {
				logger.debug("WIND_DIRECTION found"); // TODO
				windDirection = getWindDirection((Integer) ((Number)dataFields[i]).intValue());
				fieldNames.add(WIND_DIRECTION);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(windDirection);
			} else if(fieldName.equals(WIND_DIRECTION2)) {
				logger.debug("WIND_DIRECTION2 found"); // TODO
				windDirection2 = getWindDirection2((Integer) ((Number)dataFields[i]).intValue());
				fieldNames.add(WIND_DIRECTION2);
				dataTypes.add(DataTypes.DOUBLE);
				datas.add(windDirection2);
			} else if(fieldName.equals(FOO)) {
				foo = (Short) dataFields[i];
				fieldNames.add(FOO);
				dataTypes.add(DataTypes.SMALLINT);
				datas.add(foo);
			}
			i++;
		}

		StreamElement out = new StreamElement( 
				fieldNames.toArray(new String[] {}) , 
				dataTypes.toArray(new Byte[] {}),
				datas.toArray(new Serializable[] {}),
				System.currentTimeMillis() );
		dataProduced( out, true );//flexibile output.
	}

	public double getSoilTemperature (short rawValue) {
		return ((double)rawValue - 400.0) / 10.0;
	}

	public double getSoilConductivity (short rawValue) {
		if (rawValue <= 1000) return rawValue / 10;
		return (rawValue / 10) - 90;
	}

	public double getRainMeter ( short rawValue ) {
		return rawValue * 0.254;
	}

	public double getWatermark (int rawGot, double temperature) {
		double rawValue = rawGot * 1500.0 / 4095.0;

		if ( rawValue >= 200 && temperature != NO_VALUE ) {

			double p1 = -3.171e-23;
			double p2 = 1.868e-19;
			double p3 = -4.779e-16;
			double p4 = 6.957e-13;
			double p5 = -6.337e-10;
			double p6 = 3.735e-7;
			double p7 = -0.0001421;
			double p8 = 0.03357;
			double p9 = -4.463;
			double p10 = 258.4;

			double T = p1 * Math.pow( rawValue , 9 ) + p2 * Math.pow( rawValue , 8 ) + p3 * Math.pow( rawValue , 7 ) + p4 * Math.pow( rawValue , 6 ) + p5 * Math.pow( rawValue , 5 ) + p6
			* Math.pow( rawValue , 4 ) + p7 * Math.pow( rawValue , 3 ) + p8 * Math.pow( rawValue , 2 ) + p9 * rawValue + p10;

			T = Math.pow( 10 , T ) / 1000.0;

			if ( T <= 1 ) {
				T = -20.0 * ( T * ( 1.0 + 0.018 * ( temperature - 24.0 ) ) - 0.55 );
			} else if ( T <= 8 ) {
				T = ( -3.213 * T - 4.093 ) / ( 1.0 - 0.009733 * T - 0.01205 * temperature );
			} else {
				T = -2.246 - 5.239 * T * ( 1.0 + 0.018 * ( temperature - 24.0 ) ) - 0.06756 * T * T * Math.pow( 1.0 + 0.018 * ( temperature - 24.0 ) , 2 );
			}
			return T;
		} else {
			return NO_VALUE;
		}
	}

	public double getSoilMoisture (int rawValue) {
		if ( rawValue >= 400 ) {
			return ( ( ( rawValue * 2.5 * 1.7 ) / 4095.0 ) - 0.4 ) * 100.0;
		} else {
			return NO_VALUE;
		}
	}

	public double getSolarRadiation (int rawValue) {
		return ( ( rawValue * 2.5 * 1.4545 ) / 4095.0 ) * 1000.0 / 1.67;
	}

	public double getWindDirection (int rawValue) {
		return ( ( rawValue * 2.5 * 1.4545 ) / 4095.0 ) * 360.0 / 3.3;
	}
	
	public double getWindDirection2 (int rawValue) {
		double v   = rawValue * 2.5 / 4095.0;
		double vcc = 3.0;
		double R1  = 20.0;
		double R2  = 10.0;
		double R30 = 22.0;
		double R31 = 10.0;
		double k = 360.0 / 337.0;
		double R3;
		if (R31 > 0) {
			R3 = (R30 * R31) / (R30 + R31);
		}
		else {
			R3 = R30;
		}
		double a = R1 * v;
		
		double b = 360.0 * (vcc * R3 - v * R1);
		double c = -360.0 * 360.0 * v * (R2 + R3);
		double d = Math.sqrt((b * b) - (4.0 * a * c));
		if (v > 0.0) {
			return (((d - b) / (2.0 * a)) * k) % 360.0;
		}
		else {
			return NO_VALUE;
		}
	}

	public double getWindSpeed (int rawValue) {
		return ( rawValue * 2250.0 / SAMPLING_TIME ) * 1.609 * 1000.0 / 3600.0;
	}

	public double getTemperature (int rawValue) {
		if ( rawValue != 0 ) {
			return ( rawValue * 0.01 ) - 39.6;
		} else {
			return NO_VALUE;
		}
	}

	public double getSkinTemperature (int rawValue) {
		if ( rawValue != 0 ) {
			return ( rawValue / 16.0 ) - 273.15;
		} else {
			return NO_VALUE;
		}
	}

	public double getHumidity (int rawValue, double temperature) {
		if ( rawValue != 0 && temperature != NO_VALUE ) {
			return ( ( rawValue * 0.0405 ) - 4.0 - ( 0.0000028 * rawValue * rawValue ) ) + ( ( ( rawValue * 0.00008 ) + 0.01 ) * ( temperature - 25.0 ) );
		} else {
			return NO_VALUE;
		}
	}

	public void finalize ( ) {

	}

}