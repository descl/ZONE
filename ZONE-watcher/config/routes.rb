ZONEWatcher::Application.routes.draw do
  get "twitter/addtimeline", :action => "add_timeline", :controller => "twitter"
  get "twitter", :action => "index", :controller => "twitter"

  get "sources/langs", :action => "langs", :controller => "sources"
  get "sources/themes", :action => "themes", :controller => "sources"

  resources :sources
  match 'sources/:id/edit' => 'sources#edit', :constraints  =>  {:id =>  /.*/ }

  #resources :authentications

  devise_for :users, :controllers => { :omniauth_callbacks => "users/omniauth_callbacks" }
  #match '/auth/:provider/callback' => 'authentications#create'

  resources :oldsources

  get "home/index"

  resources :items
  get "filters/getNumber"

  resources :filters

  root :to => "home#index"
  get "items/index", :action => "index", :controller => "items"
  #match 'items/:id/:filter' => 'items#show'
  match 'items/:id' => 'items#show', :constraints  =>  {:id =>  /.*/ }


end

