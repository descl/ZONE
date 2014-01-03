ZONEWatcher::Application.routes.draw do

  resources :stats


  #resources :favorites
  get "favorites/create", :action => "create", :controller => "favorites"
  get "favorites/delete", :action => "destroy", :controller => "favorites"
  get "favorites", :action => "index", :controller => "favorites"

  resources :search_filters

  get '/searches/create' => 'searches#create',
        :via => :post
  get '/searches/selectSources' => 'searches#selectSources'
  get '/searches/tagsCloud/:id/:start/:end' => 'searches#tagsCloud'
  resources :searches
  get '/search/:search/getNewsNumber(/:minDate)(/:maxDate)' => 'searches#getNewsNumber'


  #Twitter managment
  get "twitter/addtimeline", :action => "add_timeline", :controller => "twitter"
  get "twitter", :action => "index", :controller => "twitter"

  #Sources managment
  get "sources/langs", :action => "langs", :controller => "sources"
  get "sources/themes", :action => "themes", :controller => "sources"
  get "sources/changeCategory", :action => "changeCategory", :controller => "sources"
  match "sources/uploadopml" => "sources#uploadopml"

  match 'sources/:id/edit' => 'sources#edit', :constraints  =>  {:id =>  /.*/ }
  match 'sources/:id/delete' => 'sources#destroy', :constraints  =>  {:id =>  /.*/ }
  match 'sources/new' => 'sources#new', :constraints  =>  {:id =>  /.*/ }
  match 'sources/:id' => 'sources#update', :constraints  =>  {:id =>  /.*/ }
  resources :sources


  #users managment
  devise_for :users, :controllers => { :omniauth_callbacks => "users/omniauth_callbacks" }
  #match '/auth/:provider/callback' => 'authentications#create'


  resources :oldsources

  get "home/index"

  get "items/deleteTag", :action => "deleteTag", :controller => "items"
  get "items/addTag", :action => "addTag", :controller => "items"
  get "items/index", :action => "index", :controller => "items"
  resources :items
  match 'items/:id' => 'items#show', :constraints  =>  {:id =>  /.*/ }


  get "filters/list", :action => "list", :controller => "filters"
  get "filters/getNumber"
  resources :filters

  root :to => "home#index"

  get "linked_words/:desc", :action => "listWords", :controller => "linked_words"
  get "complete_entities/:desc", :action => "autoComplete", :controller => "linked_words"

end

