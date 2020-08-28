package maxzawalo.c2.full.data.blockchain;

import java.util.HashMap;
import java.util.Map;

import maxzawalo.c2.base.bo.BO;

//http://www.xorbin.com/tools/sha256-hash-calculator

//Надо учесть что править могут несколько человек, поэтому ссылку надо делать на последнее изменение или 
//при подписи учитывать последнюю правку. Что делать с остальными?

//При изменении в транзакции надо будет искать ВСЮ цепочку, а не только ссылающиеся на 1. 

public class BlockChain {
	protected Map<String, String> variables = new HashMap<>();
	protected BO bo;

	public BlockChain(BO bo) {
		this.bo = bo;
	}

	protected void AddVariable(String var, Object value) {
		variables.put(var, "" + value);
	}

	public String Create(String pattern) throws Exception {
		String bch = pattern;
		for (String var : variables.keySet()) {
			// .replaceAll("\\" + var + "\\b", (value == null ? "" : value));
			String value = variables.get(var);
			if (value == null || value.trim().equals(""))
				throw new Exception("Переменная [" + var + "] для hash не заполнена");
			bch = bch.replace(var, value);
		}
		return bch;
	}
}