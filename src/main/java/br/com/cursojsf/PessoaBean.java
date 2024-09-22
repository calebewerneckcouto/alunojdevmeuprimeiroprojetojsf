package br.com.cursojsf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

import br.com.dao.DAOGeneric;
import br.com.entidades.Pessoa;
import br.com.repository.IDaoPessoa;
import br.com.repository.IDaoPessoaImpl;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {
	
	private Pessoa pessoa = new Pessoa();
	private DAOGeneric<Pessoa> daoGeneric = new DAOGeneric<Pessoa>();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	
	private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();
	
	
	public String salvar() {
		
		pessoa = daoGeneric.merge(pessoa); // salva e atualiza
		listar();
		mostrarMsg("Cadastrado com sucesso!");
		
		/*
		daoGeneric.salvar(pessoa);
		pessoa = new Pessoa();
		*/
		
		return ""; // permanecer na mesma pagina ao salvar.
	}

	
	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
		
	}


	public String novo() {
		pessoa = new Pessoa();
		return "";
	}
	
	public String deletar() {
		daoGeneric.deletePorId(pessoa);
		pessoa = new Pessoa();
		listar();
		mostrarMsg("Removido com sucesso.");
		return "";
	}
	
	@PostConstruct 
	public void listar() {
		pessoas = daoGeneric.getListGeneric(Pessoa.class);
	}
	
	
	public String logar() {
		System.out.println(pessoa.getLogin()+" - "+ pessoa.getSenha());
		Pessoa pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(),pessoa.getSenha());
		
		if (pessoaUser != null) {// achou o usuário

			// adicionar o usuário na sessão -> usuarioLogado do FilterAutentica
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", pessoaUser);

			return "primeirapagina.jsf";
		}

		return "index.jsf";
	}
	
	public String deslogar() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("usuarioLogado");
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) context.getCurrentInstance().
				getExternalContext().getRequest();
		
		httpServletRequest.getSession().invalidate();
		
		return "index.jsf";
	}
	
	public boolean permiteAcesso(String acesso) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");

		return pessoaUser.getPerfilUser().equals(acesso);
	}
	
	public void pesquisaCep(AjaxBehaviorEvent event) {
	    String cep = pessoa.getCep().replaceAll("-", ""); // Remove hyphens for consistency
	    if (cep.length() == 8) { // Validate length of the CEP
	        try {
	            // Connect to the web service
	            URL url = new URL("https://viacep.com.br/ws/" + cep + "/json/");
	            URLConnection connection = url.openConnection();
	            InputStream inputStream = connection.getInputStream();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	            StringBuilder response = new StringBuilder();
	            String line;

	            // Read the response
	            while ((line = reader.readLine()) != null) {
	                response.append(line);
	            }
	            reader.close();

	            // Process JSON response (you can use a JSON library like Gson or Jackson)
	            String jsonResponse = response.toString();
	            if (!jsonResponse.contains("erro")) {
	                // Assuming you have appropriate setter methods in Pessoa for the fields
	                // Use a JSON library to parse the response into the relevant fields
	                // Example with Gson (you need to add Gson dependency)
	                Gson gson = new Gson();
	                Endereco endereco = gson.fromJson(jsonResponse, Endereco.class);
	                
	                pessoa.setLogradouro(endereco.getLogradouro());
	                pessoa.setBairro(endereco.getBairro());
	                pessoa.setLocalidade(endereco.getLocalidade());
	                pessoa.setUf(endereco.getUf());
	            } else {
	                mostrarMsg("CEP não encontrado!");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            mostrarMsg("Erro ao buscar o CEP!");
	        }
	    } else {
	        mostrarMsg("CEP inválido! Deve ter 8 dígitos.");
	    }
	}

	// Class to represent the address data returned from the API
	class Endereco {
	    private String logradouro;
	    private String bairro;
	    private String localidade;
	    private String uf;

	    // Getters and Setters
	    public String getLogradouro() {
	        return logradouro;
	    }
	    public void setLogradouro(String logradouro) {
	        this.logradouro = logradouro;
	    }
	    public String getBairro() {
	        return bairro;
	    }
	    public void setBairro(String bairro) {
	        this.bairro = bairro;
	    }
	    public String getLocalidade() {
	        return localidade;
	    }
	    public void setLocalidade(String localidade) {
	        this.localidade = localidade;
	    }
	    public String getUf() {
	        return uf;
	    }
	    public void setUf(String uf) {
	        this.uf = uf;
	    }
	}

	
	
	
	
	
	public List<Pessoa> getPessoas() {
		return pessoas;
	}


	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}


	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public DAOGeneric<Pessoa> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DAOGeneric<Pessoa> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*private String nome;
	private String senha;
	private String texto;
	
	private HtmlCommandButton commandButton;
	
	private List<String> nomes = new ArrayList<String>();
	
	public String addNome() {
		nomes.add(nome);
		
		if(nomes.size() > 3) {
			commandButton.setDisabled(true);
			return "paginanavegada?faces-redirect=true"; // navegacao dinamica
		}
		
		return ""; // null ou vazio fica na mesma pagina -> outcome
	}
	
	
	public String getSenha() {
		return senha;
	}


	public void setSenha(String senha) {
		this.senha = senha;
	}


	public String getTexto() {
		return texto;
	}


	public void setTexto(String texto) {
		this.texto = texto;
	}


	public void setCommandButton(HtmlCommandButton commandButton) {
		this.commandButton = commandButton;
	}
	
	public HtmlCommandButton getCommandButton() {
		return commandButton;
	} 
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
		
	public void setNomes(List<String> nomes) {
		this.nomes = nomes;
	}
	
	public List<String> getNomes() {
		return nomes;
	}*/
}
