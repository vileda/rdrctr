ALTER TABLE ONLY redirect ALTER COLUMN id SET DEFAULT nextval('redirect_id_seq'::regclass);
ALTER TABLE ONLY redirectlog ALTER COLUMN id SET DEFAULT nextval('redirectlog_id_seq'::regclass);
DELETE FROM redirect where fromhost ilike '%test%';
INSERT INTO redirect(fromhost, tohost, viewcount, createdat, updatedat) VALUES ('testhost.de', 'testhost2.de', 0, NOW(), NOW());
INSERT INTO redirect(fromhost, tohost, viewcount, createdat, updatedat) VALUES ('testfoobar.de', 'foobar.de', 0, NOW(), NOW());
