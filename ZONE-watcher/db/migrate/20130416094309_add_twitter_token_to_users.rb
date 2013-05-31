class AddTwitterTokenToUsers < ActiveRecord::Migration
  def change
    add_column :users, :token, :string
    add_column :users, :tokenSecret, :string
  end
end
