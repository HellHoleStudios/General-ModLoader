package com.hhs.xiaomao.modloader.exception;

/**
 * 
 * Thrown when given an unformatted mod ID.<br>
 * <br>
 * A formatted ID should only contain latin letters and nunmbers.
 *  * 
 * @author XiaoMao
 *
 */
public class UnformattedModIDException extends RuntimeException {

	/**
	 * @serial -7115063505829408510L
	 */
	private static final long serialVersionUID = -7115063505829408510L;

	public UnformattedModIDException() {
		
	}

	public UnformattedModIDException(String arg0) {
		super(arg0);
	}

	public UnformattedModIDException(Throwable arg0) {
		super(arg0);
	}

	public UnformattedModIDException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public UnformattedModIDException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
