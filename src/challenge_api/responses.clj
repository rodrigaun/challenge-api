(ns challenge-api.responses)

(def field-valitation-messages
  {:required-field "Campo obrigatório não preenchido."
   :invalid-format "Formato do campo inválido."
   :invalid-cnpj "CNPJ inválido."})

(def login
  {:login-success "Login efetuado com sucesso."
   :login-error "Erro ao efetuar login."
   :login-token-error "Token inválido ou manipulado."
   :login-incorrect-error "Usuário ou senha incorreto."})

(def client
  {:client-created "Cliente cadastrado com sucesso."
   :client-updated "Cliente atualizado com sucesso."
   :client-deleted "Cliente excluído com sucesso."
   :client-update-error "Erro ao atualizar cliente."
   :client-consult-error "Erro ao consultar cliente."
   :client-create-error "Erro ao criar cliente."
   :client-delete-error "Erro ao excluir cliente."
   :client-exist-error "Cliente já existe."
   :client-not-found-error "Cliente não encontrado."})

(def product
  {:product-created "Produto cadastrado com sucesso."
   :product-updated "Produto atualizado com sucesso."
   :product-deleted "Produto excluído com sucesso."
   :product-update-error "Erro ao atualizar produto."
   :product-consult-error "Erro ao consultar produto."
   :product-create-error "Erro ao criar produto."
   :product-delete-error "Erro ao excluir produto."
   :product-not-found-error "Produto não encontrado."})

(def favorite
  {:favorite-created "Produto adicionado nos favoritos com sucesso."
   :favorite-deleted "Produto removido dos favoritos com sucesso."
   :favorite-empty-error "Lista de favoritos vazia."
   :favorite-consult-error "Erro ao consultar favoritos."
   :favorite-create-error "Erro ao adicionar produto em favoritos."
   :favorite-delete-error "Erro ao excluir produto em favoritos."
   :favorite-found-error "Produto já está em favoritos."
   :favorite-not-found-error "Produto não encontrado em favoritos."})

(def messages
  (merge field-valitation-messages
         login
         client
         product
         favorite))

(defn message
  ([message-key]
   {:message (get messages message-key)})
  ([message-key data]
   {:message (get messages message-key)
    :data data}))