import { useNavigate } from "react-router-dom";
import Header from "../components/header";
import { CaretLeft, DownloadSimple } from "phosphor-react";
import React, { useEffect, useState } from "react";
import { useAppContext } from "../contexts";
import useCsvUpload from "../hooks/cards/useCsvUpload";
import PageLoad from "../components/pageLoad";

const sampleCsv = `front,back,decks
Hello,Hallo,greetings
Thank you,Danke,
`;

const BulkUpload = () => {
  const navigate = useNavigate();
  const { selectedSubjectId, refetchCards, fetchDecks } = useAppContext();
  const { uploadCsv, loading, error } = useCsvUpload(); // 👈 use loading + error
  const [showToast, setShowToast] = useState(false);

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

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file || !selectedSubjectId) return;

    const result = await uploadCsv(selectedSubjectId, file);
    if (result) {
      await fetchDecks();
      await refetchCards();
      setShowToast(true);
      e.target.value = ""; // reset input
    }
  };

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => setShowToast(false), 2000);
      return () => clearTimeout(timer);
    }
  }, [showToast]);

  return (
    <div className="bg-green-200 min-h-screen">
      {/* ✅ Toast after upload */}
      {showToast && (
        <div className="fixed top-15 left-1/2 -translate-x-1/2 bg-yellow-200 border-2 border-black px-4 py-2 rounded shadow transition-opacity z-50">
          Cards uploaded successfully!
        </div>
      )}

      {/* ✅ Spinner while uploading */}
      {loading && <PageLoad />}

      <Header />
      <div className="flex justify-center">
        <div className="bg-white w-full max-w-screen-sm border-black border-2 p-4 rounded m-4">
          {/* Header with Back Button */}
          <div className="relative flex items-center h-12 mb-4">
            <div className="absolute left-0">
              <button
                id="decks-back-button"
                className="cursor-pointer"
                onClick={() => navigate("/")}
                disabled={loading} // 👈 disable while uploading
              >
                <CaretLeft size={24} />
              </button>
            </div>
            <h1 className="text-xl font-bold text-center mx-auto">
              Upload Cards
            </h1>
          </div>

          {/* Instructions */}
          <p className="text-md mb-2">
            Cards can be uploaded in bulk using a CSV file.
          </p>
          <ul className="list-disc list-inside mb-4 text-sm">
            <li>
              Columns: <code>front</code>, <code>back</code>, <code>decks</code>
            </li>
            <li>
              <code>decks</code> can contain multiple decks separated by commas
            </li>
            <li>Each row represents a single card</li>
          </ul>

          {/* Download Sample Button */}
          <button
            onClick={downloadSample}
            disabled={loading} // 👈 disable while uploading
            className={`relative flex bg-sky-200 p-2 mb-4 items-center justify-between text-black rounded shadow-lg cursor-pointer hover:bg-gray-200 border-black border-2 ${
              loading ? "opacity-50 cursor-not-allowed" : ""
            }`}
          >
            <DownloadSimple
              size={20}
              className="ml-auto mr-1"
              weight="regular"
            />
            <span className="text-sm">Download Sample</span>
          </button>

          {/* File Upload Input */}
          <div className="flex flex-row justify-center gap-2 h-[45px]">
            <input
              type="file"
              accept=".csv"
              onChange={handleFileUpload}
              disabled={loading} // 👈 disable input
              className="border-2 p-2 rounded w-full h-full"
            />
          </div>

          {/* ✅ Show error if upload failed */}
          {error && (
            <div className="mt-4 text-red-600 font-medium text-center">
              {error}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default BulkUpload;
