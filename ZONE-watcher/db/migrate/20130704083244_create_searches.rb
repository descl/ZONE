class CreateSearches < ActiveRecord::Migration
  def change
    create_table :searches do |t|
      t.belongs_to :user
      t.string :name

      t.timestamps
    end
  end
end
