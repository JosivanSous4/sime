package br.com.meslin.auxiliar;

import java.util.function.Predicate;
import org.example.Model.Usuario;

/**
 * Filtro para buscar nome de usuário em uma lista de usuários
 * 
 * @author meslin
 *
 */
class SamplePredicate implements Predicate<Usuario>
{
	String username;

	/**
	 * Constrói um filtro baseado no nome do usuário
	 * @param username
	 */
	public SamplePredicate(String username)
	{
		this.username = username;
	}

	/**
	 * Verifica se o username é desse usuário
	 */
	public boolean test(Usuario usuario)
	{
		if (username.equals(usuario.getUsername())) return true;
		return false;
	}
}
