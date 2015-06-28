ALTER TABLE ONLY redirect ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
DELETE FROM redirect where fromhost ilike '%test%';
INSERT INTO redirect(fromhost, tohost, viewcount) VALUES ('testhost.de', 'testhost2.de', 0);
INSERT INTO redirect(fromhost, tohost, viewcount) VALUES ('testfoobar.de', 'foobar.de', 0);
