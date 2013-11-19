#use bundler for dependances managment
require "bundler/capistrano"

set :application, "reador"

set :scm, :git # You can set :scm explicitly or Capistrano will make an intelligent guess based on known version control directory names
# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`

server "zone-project2.inria.fr", :app, :web, :db, :primary => true

set :scm, :git

set :deploy_to, "/appli/#{application}/"

set :repository, "https://github.com/descl/ZONE.git/"

set :deploy_subdir, "ZONE-watcher"

set :deploy_via, :remote_cache

# if you want to clean up old releases on each deploy uncomment this:
# after "deploy:restart", "deploy:cleanup"

# if you're still using the script/reaper helper you will need
# these http://github.com/rails/irs_process_scripts

# If you are using Passenger mod_rails uncomment this:
namespace :deploy do
  task :start do ; end
  task :stop do ; end
  task :restart, :roles => :app, :except => { :no_release => true } do
    desc "Restart Application"
    run "#{try_sudo} touch #{File.join(current_path,'tmp','restart.txt')}"
  end

  task :symlink_shared do
    run "ln -s #{shared_path}/config.yml #{release_path}/config/config.yml"
    run "ln -s #{shared_path}/database.yml #{release_path}/config/database.yml"
    run "ln -sf #{shared_path}/footer.html #{release_path}/app/views/layouts/_footer.html.erb"

  end
end

after :deploy, "deploy:restart"

after "deploy:finalize_update", "deploy:symlink_shared"
after 'deploy:update_code', 'deploy:migrate'
set :keep_releases, 10
after "deploy:restart", "deploy:cleanup"


#clean cache
namespace :memcached do
  desc "Flushes memcached local instance"
  task :flush, :roles => [:app] do
    run("cd #{current_path} && rake memcached:flush")
  end
end



set :use_sudo, false 


#configure rvm
set :rvm_ruby_string, :local               # use the same ruby as used locally for deployment
set :rvm_autolibs_flag, "read-only"        # more info: rvm help autolibs

before 'deploy:setup', 'rvm:install_rvm'   # install RVM
before 'deploy:setup', 'rvm:install_ruby'  # install Ruby and create gemset, OR:
before 'deploy:setup', 'rvm:create_gemset' # only create gemset

#copy config file from shared folder to prod folder
require "rvm/capistrano"

#whenever config
require "whenever/capistrano"
set :whenever_command, "bundle exec whenever"

