package extractor.WikiMeta;

import org.codehaus.jackson.annotate.JsonAnySetter;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class ResponseItem {
    private static class Document {
        public static class Account {
          private String _user, _time;

            public String getTime() {
                return _time;
            }

            public void setTime(String _time) {
                this._time = _time;
            }

            public String getUser() {
                return _user;
            }

            public void setUser(String _user) {
                this._user = _user;
            }
        }

        private Account _Account;

        public Account getAccount() {
            return _Account;
        }

        public void setAccount(Account _Account) {
            this._Account = _Account;
        }

        
        public String toString(){
            return "\tAccount"+ this.getAccount();
        }
    }
    
    private Document document;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
    
    public String toString(){
        return "Response:\n"+this.getDocument();
    }
}