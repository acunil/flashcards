import Header from "../components/header";
import { DownloadSimple, UploadSimple } from "phosphor-react";
import React, { useEffect, useState } from "react";
import { useAppContext } from "../contexts";
import useCsvUpload from "../hooks/cards/useCsvUpload";
import PageLoad from "../components/pageLoad";
import PageWrapper from "../components/pageWrapper";
import ContentWrapper from "../components/contentWrapper";
import BackButton from "../components/backButton";
import Heading from "../components/heading";

const sampleCsv = `front,back,hint_front,hint_back,decks
Hello,Hallo,,,greetings
Thank you,Danke,,,greetings
Good morning,Guten Morgen,,,daily;greetings
Good night,Gute Nacht,,,daily
How are you?,Wie geht's?,,,conversation
What's your name?,Wie heißt du?,,,conversation
Numbers 1-5,1-5,,,basics
Colors: red,rot,,,basics;colors
Colors: blue,blau,,,basics;colors`;

const BulkUpload = () => {
  const { selectedSubjectId, refetchCards, fetchDecks } = useAppContext();
  const { uploadCsv, loading, error } = useCsvUpload();
  const [showToast, setShowToast] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  // Download sample CSV
  const downloadSample = () => {
    const blob = new Blob([sampleCsv], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", "sample_cards.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setSelectedFile(file);
  };

  const handleUpload = async () => {
    if (!selectedFile || !selectedSubjectId) return;

    const result = await uploadCsv(selectedSubjectId, selectedFile);
    if (result) {
      await fetchDecks();
      await refetchCards(selectedSubjectId);
      setShowToast(true);
      setSelectedFile(null); // Clear after upload
    }
  };

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => setShowToast(false), 2000);
      return () => clearTimeout(timer);
    }
  }, [showToast]);

  return (
    <PageWrapper className="bg-green-200 min-h-screen">
      {/* Toast */}
      {showToast && (
        <div className="fixed top-15 left-1/2 -translate-x-1/2 bg-yellow-200 border-2 border-black px-4 py-2 rounded shadow transition-opacity z-50">
          Cards uploaded successfully!
        </div>
      )}

      {/* Loading spinner */}
      {loading && <PageLoad />}

      <Header />
      <ContentWrapper>
        {/* Header with Back Button */}
        <div className="relative flex items-center mb-6">
          <div className="absolute left-0">
            <BackButton />
          </div>
          <Heading>Upload Cards</Heading>
        </div>

        {/* Instructions */}
        <p className="text-md mb-2">
          Cards can be uploaded in bulk using a CSV file.
        </p>
        <ul className="list-disc list-inside mb-6 text-sm">
          <li>
            Columns: <code>front</code>, <code>back</code>,{" "}
            <code>hint_front</code>, <code>hint_back</code>, <code>decks</code>
          </li>
          <li>
            <code>decks</code> can contain multiple deck names separated by
            semicolons (<code>;</code>)
          </li>
          <li>All columns must be present, even if some entries are empty</li>
          <li>Each row represents a single card</li>
        </ul>

        {/* Sample Download Section */}
        <div className="mb-6">
          <h2 className="font-semibold mb-2">Sample CSV</h2>
          <button
            onClick={downloadSample}
            disabled={loading}
            className={`flex items-center gap-2 px-4 py-2 rounded border-2 bg-sky-200 border-black hover:bg-sky-300 cursor-pointer ${
              loading ? "opacity-50 cursor-not-allowed" : ""
            }`}
          >
            <DownloadSimple size={20} weight="regular" />
            Download Sample
          </button>
        </div>

        {/* File Upload Section */}
        <div className="mb-6">
          <h2 className="font-semibold mb-2">Upload CSV</h2>
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
            <div className="flex flex-1 items-center gap-3 min-w-0">
              <label
                htmlFor="csvFile"
                className="cursor-pointer px-4 py-2 rounded border-2 border-black bg-white hover:bg-gray-200 flex-shrink-0"
              >
                Choose File
              </label>
              <input
                type="file"
                id="csvFile"
                accept=".csv"
                onChange={handleFileSelect}
                className="hidden"
                disabled={loading}
              />
              <span className="font-medium text-gray-800 text-sm sm:text-base truncate break-all">
                {selectedFile ? selectedFile.name : "No file selected"}
              </span>
            </div>
            <button
              onClick={handleUpload}
              disabled={!selectedFile || loading}
              className={`flex items-center gap-2 px-4 py-2 rounded border-2 mt-2 sm:mt-0 ${
                !selectedFile || loading
                  ? "bg-gray-300 border-gray-400 cursor-not-allowed"
                  : "bg-green-300 border-black hover:bg-green-400 cursor-pointer"
              }`}
            >
              <UploadSimple size={20} />
              Upload
            </button>
          </div>
        </div>

        {/* Show error */}
        {error && (
          <div className="mt-4 text-red-600 font-medium text-center">
            {error}
          </div>
        )}
      </ContentWrapper>
    </PageWrapper>
  );
};

export default BulkUpload;
