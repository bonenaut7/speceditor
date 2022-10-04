package by.fxg.pilesos.console;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;

public class Console {
	private static Console instance;
	private Class<?> valueStoreClass;
	private Object valueStore;
	protected Map<String, Field> fields = new HashMap<>();
	protected Map<String, Method> methods = new HashMap<>();
	public Array<Message> msgs = new Array<>();
	
	private Console(Object valueStoreObj) {
		instance = this;
		this.valueStoreClass = valueStoreObj.getClass();
		this.valueStore = valueStoreObj;
		
		for (Field field : this.valueStoreClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(ConsoleValue.class)) {
				this.fields.put(this.getFrom(field).name(), field);
			}
		}
		for (Method method : this.valueStoreClass.getDeclaredMethods()) {
			if (method.isAnnotationPresent(ConsoleValue.class)) {
				this.methods.put(this.getFrom(method).name(), method);
			}
		}
	}
	
	private Message processInput(String line) {
		String[] args = line.split(" ");
		if (args.length == 1) {
			if (this.fields.containsKey(args[0])) {
				Field field = this.fields.get(args[0]);
				ConsoleValue cVal = this.getFrom(field);
				String value = args[0] + " - \"";
				try {
				value += 
					cVal.type() == ValueType.BYTE ? field.getByte(this.valueStore) :
					cVal.type() == ValueType.SHORT ? field.getShort(this.valueStore) :
					cVal.type() == ValueType.INT ? field.getInt(this.valueStore) :
					cVal.type() == ValueType.LONG ? field.getLong(this.valueStore) :
					cVal.type() == ValueType.FLOAT ? field.getFloat(this.valueStore) :
					cVal.type() == ValueType.DOUBLE ? field.getDouble(this.valueStore) :
					cVal.type() == ValueType.BOOLEAN ? field.getBoolean(this.valueStore) :
					field.get(this.valueStore);
				} catch (Exception e) {}
				return new Message(value + "\", " + this.getFrom(this.fields.get(args[0])).desc(), 0.75F, 0.75F, 0.75F);
			} else if (this.methods.containsKey(args[0])) {
				try {
					if (this.getFrom(this.methods.get(args[0])).args()) this.methods.get(args[0]).invoke(this.valueStore, new Object[]{args});
					else this.methods.get(args[0]).invoke(this.valueStore);
				} catch (Exception e) {}
				return null;
			} else return new Message("Unknown command.", 1, 0.25F, 0.25F);
		} else if (args.length > 1) {
			if (this.fields.containsKey(args[0])) {
				Field field = this.fields.get(args[0]);
				ConsoleValue cVal = this.getFrom(field);
				try {
					switch (cVal.type()) {
						case COMMAND:	break;
						case BYTE: 		field.set(this.valueStore, Byte.valueOf(args[1])); break;
						case SHORT:		field.set(this.valueStore, Short.valueOf(args[1])); break;
						case INT:		field.set(this.valueStore, Integer.valueOf(args[1])); break;
						case LONG:		field.set(this.valueStore, Long.valueOf(args[1])); break;
						case FLOAT:		field.set(this.valueStore, Float.valueOf(args[1])); break;
						case DOUBLE:	field.set(this.valueStore, Double.valueOf(args[1])); break;
						case BOOLEAN:	field.set(this.valueStore, Boolean.valueOf(args[1])); break;
						case STRING: 
							StringBuilder sb = new StringBuilder();
							for (int i = 1; i != args.length; i++) sb.append(args[i]).append(" ");
							sb.setLength(sb.length() - 1);
							field.set(this.valueStore, sb.toString());
							break;
					}
					return new Message(line, 1, 1, 1);
				} catch (Exception e) { e.printStackTrace(); }
			} else if (this.methods.containsKey(args[0])) {
				try {
					if (this.getFrom(this.methods.get(args[0])).args()) this.methods.get(args[0]).invoke(this.valueStore, new Object[]{args});
					else this.methods.get(args[0]).invoke(this.valueStore);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			} else return new Message("Unknown command.", 1, 0.25F, 0.25F);
		}
		return new Message("Console#processInput error.", 1, 0, 0);
	}
	
	protected ConsoleValue getFrom(Field field) {
		return field.<ConsoleValue>getDeclaredAnnotation(ConsoleValue.class);
	}
	
	protected ConsoleValue getFrom(Method method) {
		return method.<ConsoleValue>getDeclaredAnnotation(ConsoleValue.class);
	}
	
	
	
	public static void create(Object valueStoreObj) {
		new Console(valueStoreObj);
	}
	
	public static Console get() {
		return instance;
	}
	
	public static void input(String line) {
		Message msg = instance.processInput(line);
		if (msg != null) instance.msgs.add(msg);
	}
	
	public static void report(String msg, float r, float g, float b) {
		instance.msgs.add(new Message(msg, r, g, b));
	}
	
	public static class Message {
		public String message;
		public float r, g, b;
		
		public Message(String message, float r, float g, float b) {
			this.message = message;
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}
}
