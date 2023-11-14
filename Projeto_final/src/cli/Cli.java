package cli;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import entidades.evento.Evento;
import entidades.evento.Exposicao;
import entidades.evento.Jogo;
import entidades.evento.Show;

import entidades.ingresso.Ingresso;
import entidades.ingresso.TipoIngresso;
import entidades.ingresso.IngExposicao;
import entidades.ingresso.IngJogo;
import entidades.ingresso.IngShow;

public class Cli {
    public static int executar() throws IOException {
        Evento evento = null;
        Ingresso ingresso = null;
        Scanner leitor = new Scanner(System.in);
        int buscaEvento = 0;
        int opcao;

        System.out.println("Seja bem-vindo ao programa de venda de ingressos de eventos!");

        while (true) {
            menu();
            opcao = leitor.nextInt();
            switch (opcao) {
                case 1:
                    evento = cadastrarEvento();
                    if(evento != null ) {
                    	System.out.println("Evento cadastrado com sucesso!");
                    }
                    break;
                case 2:
                    exibirEvento(evento);
                    break;
                case 3:
                    exibirIngressosRestantes(evento, leitor, buscaEvento);
                    break;
                case 4:
                    buscarEventoPorNome(leitor, buscaEvento);
                    break;
                case 5:
                	RemoverEventoPorNome(leitor, buscaEvento);
                    break;
                case 6:
                	AtualizarEventoPorNome(leitor, buscaEvento);
                    break;
                case 11:
                    ingresso = venderIngresso(evento, leitor, ingresso);
                    break;
                default:
                	gravarArqTxt();
                    leitor.close();
                    return 0;
            }
        }
    }

    private static void menu() {
        System.out.println("\nDigite a opção desejada ou qualquer outro valor para sair:");
        System.out.println("1 - Cadastrar um novo evento;");
        System.out.println("2 - Exibir todos os eventos cadastrados;");
        System.out.println("3 - Exibir ingressos restantes;");
        System.out.println("4 - Buscar evento pelo nome;");
        System.out.println("5 - Excluir um evento;");
        System.out.println("6 - Alterar um evento (Somente data e local);");
        System.out.println("11 - Vender um ingresso;");
    }

    private static Ingresso venderIngresso(Evento evento, Scanner leitor, Ingresso ingresso) {
    	String tipo, nomeEventoDigitado;
    	TipoIngresso tipoIngresso;
    	int quantidade;
    	boolean achou = false;
    	
        if (evento == null) {
            System.out.println("Evento ainda não foi cadastrado!");
            return null;
        }

        List<Evento> listarTdsEventos = Evento.getListaEventos();
    	if (listarTdsEventos.isEmpty()) {
    		System.out.print("Nenhum evento foi cadastrado no momento ! \n");
    		return null;
    	}  else {
    		int i = 0;
    		Scanner scn = new Scanner(System.in);
    		System.out.print("Informe o nome do evento: ");
    		nomeEventoDigitado = scn.nextLine();
    		
    		for (Evento evt : listarTdsEventos) {
    			if(evt.getNome().equalsIgnoreCase(nomeEventoDigitado)) {
    				System.out.print("Informe o tipo do ingresso (meia ou inteira): ");
    		        tipo = scn.next();
    		        if (!(tipo.equals("meia") || tipo.equals("inteira"))) {
    		            System.out.println("Tipo selecionado inválido!");
    		            return null;
    		        }

    		        tipoIngresso = tipo.equals("meia") ? TipoIngresso.MEIA : TipoIngresso.INTEIRA;

    		        System.out.print("Informe quantos ingressos você deseja: ");
    		        quantidade = scn.nextInt();

    		        if (!evento.isIngressoDisponivel(tipoIngresso, quantidade)) {
    		            System.out.println("Não há ingressos disponíveis desse tipo!");
    		            return null;
    		        }

    		        if (evento instanceof Jogo) {
    		            int percentual;

    		            System.out.print("Informe o percentual do desconto de sócio torcedor: ");
    		            percentual = scn.nextInt();
    		            ingresso = new IngJogo(evento, tipoIngresso, percentual);
    		        } else if (evento instanceof Show) {
    		            String localizacao;

    		            System.out.print("Informe a localização do ingresso (pista ou camarote): ");
    		            localizacao = scn.next();

    		            if (!(localizacao.equals("pista") || localizacao.equals("camarote"))) {
    		                System.out.println("Localização inválida!");
    		                return null;
    		            }
    		            ingresso = new IngShow(evento, tipoIngresso, localizacao);
    		        } else {
    		            String desconto;

    		            System.out.print("Informe se possui desconto social (s/n): ");
    		            desconto = scn.next();

    		            ingresso = new IngExposicao(evento, tipoIngresso, desconto.equals("s"));
    		        }
    		        
    		        achou = true;
    		        evento.venderIngresso(tipoIngresso, quantidade);
    		        System.out.println("Ingresso vendido com sucesso!");
    			}
    			
    		}
    		if(!achou) {
    			System.out.print("\n Nao existe nenhum evento cadastrado com o nome buscado! \n");
    		}
    		return ingresso;
    	}
    }

    private static void exibirIngressosRestantes(Evento evento, Scanner leitor, int buscaEvento) {
    	//buscaEvento = 0 - Consulta evento - default
    	//buscaEvento = 1 - Remove evento
    	//buscaEvento = 2 - Altera evento
    	//buscaEvento = 3 - Busca tipo ingresso meia/inteira
    	
    	buscaEvento = 3;
    	if (evento == null) {
            System.out.println("Evento ainda não foi cadastrado!");
        } else {
        	AcaoEventoGeral(leitor, buscaEvento);
        }
    }

	private static void exibirEvento(Evento evento) {
        if (evento == null) {
            System.out.println("Nenhum evento foi cadastrado no momento !");
        } else {
        	listarEvt();
        }
    }
    
    private static void buscarEventoPorNome(Scanner leitor, int buscaEvento) {
    	//buscaEvento = 0 - Consulta evento - default
    	//buscaEvento = 1 - Remove evento
    	//buscaEvento = 2 - Altera evento
    	//buscaEvento = 3 - Busca tipo ingresso meia/inteira
    	
    	AcaoEventoGeral(leitor, buscaEvento);
    }
    
    private static void RemoverEventoPorNome(Scanner leitor, int buscaEvento) {
    	//buscaEvento = 0 - Consulta evento - default
    	//buscaEvento = 1 - Remove evento
    	//buscaEvento = 2 - Altera evento
    	//buscaEvento = 3 - Busca tipo ingresso meia/inteira
    	
    	buscaEvento = 1;
    	AcaoEventoGeral(leitor, buscaEvento);
    }
    
    private static void AtualizarEventoPorNome(Scanner leitor, int buscaEvento) {
    	//buscaEvento = 0 - Consulta evento - default
    	//buscaEvento = 1 - Remove evento
    	//buscaEvento = 2 - Altera evento
    	//buscaEvento = 3 - Busca tipo ingresso meia/inteira
    	
    	buscaEvento = 2;
    	AcaoEventoGeral(leitor, buscaEvento);
    }
    

    private static void AcaoEventoGeral(Scanner leitor, int buscaEvento) {
    	//buscaEvento = 0 - Consulta evento - default
    	//buscaEvento = 1 - Remove evento
    	//buscaEvento = 2 - Altera evento
    	//buscaEvento = 3 - Busca tipo ingresso meia/inteira
    	
    	LocalDate dataFinal = null;
    	List<Evento> listarTdsEventos = Evento.getListaEventos();
    	if (listarTdsEventos.isEmpty()) {
    		System.out.print("Nenhum evento foi cadastrado no momento ! \n");
    	} else {
    		String nomeEventoDigitado = ""; 
    		String localDigitado, dataDigitada;
    		boolean achou = false;
    		Scanner scn = new Scanner(System.in);
    		System.out.print("Informe o nome do evento: ");
    		nomeEventoDigitado = scn.nextLine();
    		
    		int i = 0;
    		for (Evento evento : listarTdsEventos) {
    				if(evento.getNome().equalsIgnoreCase(nomeEventoDigitado)) {
    					if (buscaEvento == 0) {
    						System.out.print("\n Os dados do evento buscado sao : \n");
    						System.out.println(evento);
    					} else if (buscaEvento == 1) {
    						listarTdsEventos.remove(i);
    						System.out.print("\n Evento removido com sucesso \n");
    						achou = true;
    						break;
    					} else if (buscaEvento == 2){
    						System.out.println(evento);
    						System.out.print("\n O evento digitado esta listado acima. \n");
    						System.out.print("Informe o novo valor do campo local: ");
    			    		localDigitado = scn.nextLine();
    			    		System.out.print("Informe o novo valor do campo data: ");
    			    		dataDigitada = leitor.next();
    			    		DateTimeFormatter formatacao = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    			            try {
    			            	dataFinal = LocalDate.parse(dataDigitada, formatacao);
    			            	evento.setLocal(localDigitado);
    			            	evento.setDataFinal(dataFinal);
    			            	System.out.print("\n Campos do evento atualizados com sucesso \n");
    			    		} catch (Exception e) {
    			    			System.out.println("Data no formato errado ! Tente novamente ! ");
    			    			System.out.println("----------------------------------------- \n ");
    			    			achou = true;
    			    			break;
    			    		}
    					} else if (buscaEvento == 3){
    						System.out.print("\n O evento digitado apresenta:");
    						System.out.print("\n Ingresso tipo Meia: " + evento.getIngressosMeia());
    						System.out.print("\n Ingresso tipo Inteira: " + evento.getIngressosInteira() + " \n");
    						}
    					achou = true;
    				}
    				i++;
			}
    		if(!achou) {
    			System.out.print("\n Nao existe nenhum evento cadastrado com o nome buscado! \n");
    		}
    	}
		
	}

	private static List<Evento> listarEvt() {
		List<Evento> listarTdsEventos = Evento.getListaEventos();
    	if(listarTdsEventos.isEmpty()) {
    		System.out.println("Nenhum evento foi cadastrado no momento !");
    	} else {
    		for (Evento eventosCadastrados : listarTdsEventos) {
    			System.out.println(eventosCadastrados);
    		}
    	}
		return listarTdsEventos;
	}

	private static Evento cadastrarEvento() throws IOException {
        String nome = ""; 
        String local = "";
        String data, tipo;
        LocalDate dataFinal = LocalDate.now();
        int ingMeia, ingInteira;
        double preco;
        Scanner leitor = new Scanner(System.in);
        
        System.out.print("Informe o nome do evento: ");
        nome = leitor.nextLine();
        System.out.print("Informe o local do evento: ");
        local = leitor.nextLine();
        System.out.print("Informe a data do evento: (dd/mm/yyyy) ");
        data = leitor.next();
        DateTimeFormatter formatacao = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
        	dataFinal = LocalDate.parse(data, formatacao);
		} catch (Exception e) {
			System.out.println("Data no formato errado ! Tente novamente ! ");
			System.out.println("----------------------------------------- \n ");
			return null;
		}
        System.out.print("Informe o número de entradas tipo meia: ");
        ingMeia = leitor.nextInt();
        System.out.print("Informe o número de entradas tipo inteira: ");
        ingInteira = leitor.nextInt();
        System.out.print("Informe o preço cheio do evento: ");
        preco = leitor.nextDouble();
        System.out.print("Informe o tipo do evento (show, jogo ou exposição): ");
        tipo = leitor.next();

        if (tipo.equals("show")) {
        	
        	String artista = null;
            String genero = null;
            Show show = new Show(nome, dataFinal, local, ingMeia, ingInteira, preco, artista, genero);
            
            System.out.print("Informe o nome do artista: ");
            show.setArtista(leitor.next());
            System.out.print("Informe o gênero do show: ");
            show.setGenero(leitor.next());
            
            show.adicionarEvento(show);
            
            return show;
        }

        else if (tipo.equals("jogo")) {
        	
        	Jogo jogo = new Jogo(nome, dataFinal, local, ingMeia, ingInteira, preco, data, local, tipo);
        	
            System.out.print("Informe o esporte: ");
            jogo.setEsporte(leitor.next());
            System.out.print("Informe a equipe da casa: ");
            jogo.setEquipeCasa(leitor.next());
            System.out.print("Informe a equipe adversária: ");
            jogo.setEquipeAdversaria(leitor.next());
            
            jogo.adicionarEvento(jogo);
            
            return jogo;
            
        } else {

	        int idadeMin = 0, duracao = 0;
	        
	        Exposicao exposicao = new Exposicao(nome, dataFinal, local, ingMeia, ingInteira, preco, idadeMin, duracao);
	        
	        System.out.print("Informe a idade mínima para entrar na exposição: ");
	        exposicao.setFaixaEtariaMinima(leitor.nextInt());
	        System.out.print("Informe a duração em dias da exposição: ");
	        exposicao.setDuracaoDias(leitor.nextInt());
	        
	        exposicao.adicionarEvento(exposicao);
	        
	        return exposicao;
        }
    }
    
    private static void gravarArqTxt() throws IOException {
    	try {
    		FileWriter arq = new FileWriter("c:\\Projeto_Eventos.txt");
    		PrintWriter gravarArq = new PrintWriter(arq);
    		List<Evento> eventos = listarEvt();
    		
	   	 	gravarArq.printf("+--Resultado--+%n");
	   	 		if(eventos.isEmpty()) {
	   	 			gravarArq.println("Nao existem eventos cadastrados. \n");
	   	 			System.out.println("Nao existem eventos cadastrados para salvar no txt. \n" + 
	   	 				"Arquivo gerado em c:\\Projeto_Eventos.txt. \n" + "Volte sempre !");
	   	 		} else {
	   	 			gravarArq.println(eventos);
	   	 			System.out.println("\n Os dados dos eventos acima foram gravados com sucesso no caminho c:\\Projeto_Eventos.txt \r\n" + 
	   	 					"Volte sempre !");
	   	 		}
	   	 	gravarArq.printf("+----Fim do arquivo---------+%n");
    	    arq.close();
    	    
		} catch (Exception e) {
			System.out.println("Sem permissao de acesso para gravar o arquivo. \n" +
					"Por favor, execute o programa como administrador do sistema para sanar o problema de acesso.");
		} 
    	
    }
}
