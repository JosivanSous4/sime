package br.com.meslin;

/**
 * Para compilar:
 * $ javac -classpath .:/media/meslin/16E65872E6585459/Program\ Files/Java/ContexNet/'*' -d . br/com/meslin/DefineGrupo.java
 * 
 * Para executar:
 * $ java  -classpath .:/media/meslin/16E65872E6585459/Program\ Files/Java/ContexNet/'*'      br.com.meslin.DefineGrupo nomes.txt
 */

import br.com.meslin.auxiliar.SelecionaGrupo;
import lac.cnet.groupdefiner.components.GroupDefiner;
import lac.cnet.groupdefiner.components.groupselector.GroupSelector;
/**
 * Implementa a definição e seleção de grupos baseadas em regiões
 * <p>
 * Uso: DefineGrupo <nome do arquivo de arquivos de grupo>
 * 
 * @author meslin
 *
 */
public class DefineGrupo {

	public static void main(String[] args) {
		System.err.println("[DefineGrupo." + new Object(){}.getClass().getEnclosingMethod().getName() + "]");
		if(args.length > 1)
		{
			System.err.println("Uso: DefineGrupo <nome do arquivo de arquivos de grupo>");
			return;
		}
		
		String nomeArquivo = null;
		if(args.length == 0) nomeArquivo = "nomes.txt";
		else nomeArquivo = args[0];

		GroupSelector selecionaGrupo = new SelecionaGrupo(nomeArquivo);
		new GroupDefiner(selecionaGrupo);
		try {
			Thread.sleep(Long.MAX_VALUE);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}