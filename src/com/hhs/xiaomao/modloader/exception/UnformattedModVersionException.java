package com.hhs.xiaomao.modloader.exception;

/**
 * 
 * Thrown when given an unformatted mod version.<br>
 * <br>
 * A formatted version should be like this:<br>
 * <code>1.8.0</code>
 * 
 * @author XiaoMao
 *
 */
public class UnformattedModVersionException extends RuntimeException {


	/**
	 * @serial 8135889231841312991
	 */
	private static final long serialVersionUID = 8135889231841312991L;

	public UnformattedModVersionException() {
		
	}

	public UnformattedModVersionException(String arg0) {
		super(arg0);
	}

	public UnformattedModVersionException(Throwable arg0) {
		super(arg0);
	}

	public UnformattedModVersionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public UnformattedModVersionException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
